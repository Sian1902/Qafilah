package com.example.qafilah.features.cart.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.util.RequireAuth
import com.example.qafilah.features.cart.domain.model.CartItemCommand
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.usecase.ApplyDiscountUseCase
import com.example.qafilah.features.cart.domain.usecase.FetchCartUseCase
import com.example.qafilah.features.cart.domain.usecase.ManageCartItemUseCase
import com.example.qafilah.features.cart.domain.usecase.ObserveCartStateUseCase
import com.example.qafilah.features.cart.domain.usecase.RemoveDiscountUseCase
import com.example.qafilah.features.cart.presentation.contract.CartEvent
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.contract.CartUIState
import com.example.qafilah.features.catalog.domain.usecases.ClearPendingAdCouponUseCase
import com.example.qafilah.features.catalog.domain.usecases.GetPendingAdCouponUseCase
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


private data class SyncPayload(
    val command: CartItemCommand,
    val fallbackCart: StoreCart
)

class CartViewModel(
    private val requireAuth: RequireAuth,
    private val observeCartStateUseCase: ObserveCartStateUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val manageCartItemUseCase: ManageCartItemUseCase,
    private val applyDiscountUseCase: ApplyDiscountUseCase,
    private val removeDiscountUseCase: RemoveDiscountUseCase,
    private val getPendingAdCouponUseCase: GetPendingAdCouponUseCase,
    private val clearPendingAdCouponUseCase: ClearPendingAdCouponUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CartUIState())
    val state: StateFlow<CartUIState> = _state.asStateFlow()

    private val _events = Channel<CartEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val syncJobs = mutableMapOf<String, Job>()
    private val fallbackStates = mutableMapOf<String, StoreCart>()

    init {
        viewModelScope.launch {
            observeCartStateUseCase().collect { storeCart ->
                _state.update { it.copy(cart = storeCart) }
            }
        }
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.EnterScreen -> checkAuthAndLoad()
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
            is CartIntent.UpdateDiscountInput -> {
                _state.update { it.copy(discountInput = intent.code, discountError = null) }
            }
            is CartIntent.ApplyDiscountCode -> handleApplyDiscount()
            is CartIntent.RemoveDiscountCode -> handleRemoveDiscount(intent.code)
        }
    }

    private fun checkAuthAndLoad() {
        requireAuth.invoke(
            onAuthenticated = { loadCart() },
            onGuest = { _state.update { it.copy(showLoginPrompt = true) } }
        )
    }

    private fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                fetchCartUseCase()
                checkAndApplyPendingCoupon()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = e.message ?: "Failed to load cart"
                    )
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleIncreaseQuantity(lineId: String) {
        val currentCart = _state.value.cart ?: return
        val currentQuantity = currentLineQuantity(lineId) ?: return
        val newQuantity = currentQuantity + 1

        executeOptimisticUpdate(lineId, newQuantity, currentCart)
    }

    private fun handleDecreaseQuantity(lineId: String) {
        val currentCart = _state.value.cart ?: return
        val currentQuantity = currentLineQuantity(lineId) ?: return

        if (currentQuantity <= 1) {
            handleRemoveItem(lineId)
            return
        }

        val newQuantity = currentQuantity - 1
        executeOptimisticUpdate(lineId, newQuantity, currentCart)
    }

    private fun handleRemoveItem(lineId: String) {
        val currentCart = _state.value.cart ?: return

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
        return _state.value.cart?.lines?.firstOrNull { it.id == lineId }?.quantity
    }

    private fun handleApplyDiscount() {
        val codeToApply = _state.value.discountInput.trim()
        if (codeToApply.isEmpty()) return

        _state.update { it.copy(isApplyingDiscount = true, discountError = null) }

        viewModelScope.launch {
            try {
                applyDiscountUseCase(codeToApply)

                _state.update { it.copy(
                    isApplyingDiscount = false,
                    discountInput = ""
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    isApplyingDiscount = false,
                    discountError = e.message ?: "Failed to apply discount"
                )}
            }
        }
    }

    private fun handleRemoveDiscount(codeToRemove: String) {
        _state.update { it.copy(isApplyingDiscount = true, discountError = null) }

        viewModelScope.launch {
            try {
                removeDiscountUseCase(codeToRemove)
                _state.update { it.copy(isApplyingDiscount = false) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isApplyingDiscount = false,
                    errorMessage = e.message ?: "Failed to remove discount"
                )}
            }
        }
    }

    private suspend fun checkAndApplyPendingCoupon() {
        val currentCart = _state.value.cart
        if (currentCart == null || currentCart.lines.isEmpty()) return

        val pendingCodeResult = getPendingAdCouponUseCase()
        val pendingCode = pendingCodeResult.getOrNull()

        if (!pendingCode.isNullOrEmpty()) {
            try {
                _state.update { it.copy(isApplyingDiscount = true) }

                applyDiscountUseCase(pendingCode)

                clearPendingAdCouponUseCase()

                _state.update { it.copy(
                    isApplyingDiscount = false,
                    discountInput = ""
                )}


            } catch (e: Exception) {
                clearPendingAdCouponUseCase()

                _state.update { it.copy(
                    isApplyingDiscount = false,
                    discountError = "Your saved promo code $pendingCode is no longer valid."
                )}
            }
        }
    }
}
