package com.example.qafilah.features.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.cart.domain.model.CartItemCommand
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.usecase.FetchCartUseCase
import com.example.qafilah.features.cart.domain.usecase.ManageCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.cart.presentation.contract.CartEvent
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.contract.CartLineUiModel
import com.example.qafilah.features.cart.presentation.contract.CartUIState
import com.example.qafilah.features.cart.presentation.contract.CartUiModel
import com.example.qafilah.core.currency.ConvertPriceUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

class CartViewModel(
    private val requireAuth: RequireAuth,
    private val observeCartStateUseCase: ObserveCartStateUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val manageCartItemUseCase: ManageCartItemUseCase,
    private val convertPriceUseCase: ConvertPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartUIState())
    val state: StateFlow<CartUIState> = _state.asStateFlow()

    private val _events = Channel<CartEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val syncJobs = mutableMapOf<String, Job>()
    private val fallbackStates = mutableMapOf<String, StoreCart>()
    private var rawCart: StoreCart? = null

    init {
        viewModelScope.launch {
            observeCartStateUseCase().collect { storeCart ->
                rawCart = storeCart
                if (storeCart != null) {
                    val uiModel = storeCart.toUiModel()
                    _state.update { it.copy(cart = uiModel) }
                } else {
                    _state.update { it.copy(cart = null) }
                }
            }
        }
    }

    private suspend fun StoreCart.toUiModel(): CartUiModel {
        return CartUiModel(
            totalQuantity = totalQuantity,
            displaySubtotal = convertPriceUseCase(cost.subtotalAmount.amount.toDouble()),
            displayTotal = convertPriceUseCase(cost.totalAmount.amount.toDouble()),
            checkoutUrl = checkoutUrl,
            lines = lines.map { line ->
                CartLineUiModel(
                    id = line.id,
                    title = line.merchandise.product.title,
                    vendor = line.merchandise.product.vendor,
                    quantity = line.quantity,
                    displayPrice = convertPriceUseCase(line.cost.amountPerQuantity.amount.toDouble()),
                    displayTotal = convertPriceUseCase(line.cost.totalAmount.amount.toDouble()),
                    imageUrl = line.merchandise.image?.url,
                    variantTitle = line.merchandise.title
                )
            }
        )
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.EnterScreen -> checkAuthAndLoad(intent.fallbackErrorMessage)
            is CartIntent.NavigateToLogin -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToLogin)
            }
            is CartIntent.NavigateToSignUp -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToSignUp)
            }
            is CartIntent.DismissLoginPrompt -> {
                _state.update { it.copy(showLoginPrompt = false) }
                _events.trySend(CartEvent.NavigateToHome)
            }

            is CartIntent.IncreaseQuantity -> handleIncreaseQuantity(intent.lineId)
            is CartIntent.DecreaseQuantity -> handleDecreaseQuantity(intent.lineId)
            is CartIntent.RemoveItem -> handleRemoveItem(intent.lineId)
            is CartIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun checkAuthAndLoad(fallbackErrorMessage: String) {
        requireAuth.invoke(
            onAuthenticated = { loadCart(fallbackErrorMessage) },
            onGuest = { _state.update { it.copy(showLoginPrompt = true) } }
        )
    }

    private fun loadCart(fallbackErrorMessage: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                fetchCartUseCase()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = e.message ?: fallbackErrorMessage
                    )
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleIncreaseQuantity(lineId: String) {
        val currentCart = rawCart ?: return
        val currentQuantity = currentLineQuantity(lineId) ?: return
        val newQuantity = currentQuantity + 1

        executeOptimisticUpdate(lineId, newQuantity, currentCart)
    }

    private fun handleDecreaseQuantity(lineId: String) {
        val currentCart = rawCart ?: return
        val currentQuantity = currentLineQuantity(lineId) ?: return

        if (currentQuantity <= 1) {
            handleRemoveItem(lineId)
            return
        }

        val newQuantity = currentQuantity - 1
        executeOptimisticUpdate(lineId, newQuantity, currentCart)
    }

    private fun handleRemoveItem(lineId: String) {
        val currentCart = rawCart ?: return

        if (!fallbackStates.containsKey(lineId)) {
            fallbackStates[lineId] = currentCart
        }

        viewModelScope.launch {
            manageCartItemUseCase(CartItemCommand.OptimisticRemove(lineId))
        }

        scheduleSync(lineId, CartItemCommand.SyncRemoteRemoval(lineId))
    }

    private fun executeOptimisticUpdate(lineId: String, newQuantity: Int, currentCart: StoreCart) {
        if (!fallbackStates.containsKey(lineId)) {
            fallbackStates[lineId] = currentCart
        }

        viewModelScope.launch {
            manageCartItemUseCase(CartItemCommand.OptimisticUpdate(lineId, newQuantity))
        }

        scheduleSync(lineId, CartItemCommand.SyncRemoteQuantity(lineId, newQuantity))
    }

    private fun scheduleSync(lineId: String, command: CartItemCommand) {
        syncJobs[lineId]?.cancel()

        syncJobs[lineId] = viewModelScope.launch {
            delay(500L.milliseconds)
            try {
                manageCartItemUseCase(command)
                fallbackStates.remove(lineId)
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                val fallback = fallbackStates.remove(lineId)
                if (fallback != null) {
                    manageCartItemUseCase(CartItemCommand.Rollback(fallback))
                }
                _state.update { it.copy(errorMessage = e.message) }
            } finally {
                syncJobs.remove(lineId)

                if (syncJobs.isEmpty()) {
                    fetchFinalCartState()
                }
            }
        }
    }

    private fun fetchFinalCartState() {
        viewModelScope.launch {
            try {
                fetchCartUseCase()
            } catch (e: Exception) {

            }
        }
    }

    private fun currentLineQuantity(lineId: String): Int? {
        return rawCart?.lines?.firstOrNull { it.id == lineId }?.quantity
    }
}
