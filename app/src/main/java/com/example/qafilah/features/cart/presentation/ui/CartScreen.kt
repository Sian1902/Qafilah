package com.example.qafilah.features.cart.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.features.cart.domain.model.Money
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.presentation.contract.CartEvent
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import com.example.ui_kit.components.cart.AddDiscountDialog
import com.example.ui_kit.components.cart.CartItemCard
import com.example.ui_kit.components.cart.CartSummaryCard
import com.example.ui_kit.components.cart.CartEmptyView
import com.example.ui_kit.components.cart.DiscountCodesCard
import com.example.ui_kit.components.login.LoginPromptBottomSheet
import org.koin.androidx.compose.koinViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {},
    viewModel: CartViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDiscountDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(CartIntent.EnterScreen)
        viewModel.events.collect { event ->
            when (event) {
                CartEvent.NavigateToLogin -> onNavigateToLogin()
                CartEvent.NavigateToSignUp -> onNavigateToSignUp()
                CartEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        state.cart?.let { cart ->
            CartContent(
                modifier = Modifier.fillMaxSize(),
                storeCart = cart,
                onIncreaseQuantity = { lineId ->
                    viewModel.onIntent(CartIntent.IncreaseQuantity(lineId))
                },
                onDecreaseQuantity = { lineId ->
                    viewModel.onIntent(CartIntent.DecreaseQuantity(lineId))
                },
                onRemoveItem = { lineId ->
                    viewModel.onIntent(CartIntent.RemoveItem(lineId))
                },
                onCheckout = onNavigateToCheckout,
                onAddDiscountClick = {
                    viewModel.onIntent(CartIntent.DismissError)
                    showDiscountDialog = true
                },
                onRemoveDiscount = { code -> viewModel.onIntent(CartIntent.RemoveDiscountCode(code)) }
            )
        }

        if (state.isLoading && state.cart == null) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp
            )
        }
    }

    if (state.showLoginPrompt) {
        LoginPromptBottomSheet(
            featureName = "your cart",
            onDismiss = { viewModel.onIntent(CartIntent.DismissLoginPrompt) },
            onNavigateToLogin = { viewModel.onIntent(CartIntent.NavigateToLogin) },
            onNavigateToSignUp = { viewModel.onIntent(CartIntent.NavigateToSignUp) }
        )
    }

    if (showDiscountDialog) {
        AddDiscountDialog(
            inputValue = state.discountInput,
            onInputValueChange = { viewModel.onIntent(CartIntent.UpdateDiscountInput(it)) },
            isApplying = state.isApplyingDiscount,
            errorMessage = state.discountError,
            onApply = { viewModel.onIntent(CartIntent.ApplyDiscountCode) },
            onDismiss = { showDiscountDialog = false }
        )
    }

    LaunchedEffect(state.isApplyingDiscount) {
        if (!state.isApplyingDiscount && state.discountError == null && state.discountInput.isEmpty()) {
            showDiscountDialog = false
        }
    }
}

@Composable
private fun CartContent(
    modifier: Modifier = Modifier,
    storeCart: StoreCart,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    onAddDiscountClick: () -> Unit,
    onRemoveDiscount: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    if (storeCart.lines.isEmpty()) {
        CartEmptyView(modifier = modifier.padding(16.dp))
    } else {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(storeCart.lines, key = { it.id }) { line ->
                    CartItemCard(
                        imageUrl = line.merchandise.image?.url.orEmpty(),
                        title = line.merchandise.product.title,
                        price = line.cost.totalAmount.toDisplayString(),
                        quantity = line.quantity,
                        quantityAvailable = line.merchandise.quantityAvailable,
                        onIncreaseQuantity = { onIncreaseQuantity(line.id) },
                        onDecreaseQuantity = { onDecreaseQuantity(line.id) },
                        onRemoveItem = { onRemoveItem(line.id) }
                    )
                }
            }

            DiscountCodesCard(
                appliedCodes = storeCart.appliedDiscounts.map { it.code },
                onAddClick = onAddDiscountClick,
                onRemoveDiscount = onRemoveDiscount
            )

            CartSummaryCard(
                subTotalAmount = storeCart.cost.subtotalAmount.toDisplayString(),
                totalAmount = storeCart.cost.totalAmount.toDisplayString(),
                totalTaxAmount = storeCart.cost.totalTaxAmount?.toDisplayString(),
                checkoutChargeAmount = storeCart.cost.checkoutChargeAmount.toDisplayString(),
                currencyCode = storeCart.cost.totalAmount.currencyCode
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Proceed to Checkout",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun Money.toDisplayString(): String {
    return amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
}
