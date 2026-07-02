package com.example.qafilah.features.cart.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
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
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.presentation.contract.CartEvent
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import com.example.ui_kit.components.cart.CartEmptyView
import com.example.ui_kit.components.cart.CartItemCard
import com.example.ui_kit.components.cart.CartSummaryCard
import com.example.ui_kit.components.cart.DiscountCodesCard
import org.koin.androidx.compose.koinViewModel
import java.math.RoundingMode

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
                storeCart = cart,
                onIncreaseQuantity = { viewModel.onIntent(CartIntent.IncreaseQuantity(it)) },
                onDecreaseQuantity = { viewModel.onIntent(CartIntent.DecreaseQuantity(it)) },
                onRemoveItem = { viewModel.onIntent(CartIntent.RemoveItem(it)) },
                onAddDiscountClick = {
                    viewModel.onIntent(CartIntent.DismissError)
                    showDiscountDialog = true
                },
                onRemoveDiscount = { viewModel.onIntent(CartIntent.RemoveDiscountCode(it)) },
                onCheckout = onNavigateToCheckout
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

    CartOverlays(
        state = state,
        viewModel = viewModel,
        showDiscountDialog = showDiscountDialog,
        onDismissDiscountDialog = { showDiscountDialog = false }
    )
}


@Composable
private fun CartContent(
    modifier: Modifier = Modifier,
    storeCart: StoreCart,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddDiscountClick: () -> Unit,
    onRemoveDiscount: (String) -> Unit,
    onCheckout: () -> Unit
) {
    if (storeCart.lines.isEmpty()) {
        CartEmptyView(modifier = modifier.fillMaxSize().padding(16.dp))
        return
    }

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(storeCart.lines, key = { it.id }) { line ->
                CartItemCard(
                    imageUrl = line.merchandise.image?.url.orEmpty(),
                    title = line.merchandise.product.title,
                    price = line.cost.totalAmount.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    currencyCode = line.cost.totalAmount.currencyCode,
                    quantity = line.quantity,
                    quantityAvailable = line.merchandise.quantityAvailable,
                    onIncreaseQuantity = { onIncreaseQuantity(line.id) },
                    onDecreaseQuantity = { onDecreaseQuantity(line.id) },
                    onRemoveItem = { onRemoveItem(line.id) }
                )
            }

            item {
                DiscountCodesCard(
                    appliedCodes = storeCart.appliedDiscounts.map { it.code },
                    onAddClick = onAddDiscountClick,
                    onRemoveDiscount = onRemoveDiscount
                )
            }

            item {
                CartSummaryCard(
                    subTotalAmount = storeCart.cost.subtotalAmount.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    totalAmount = storeCart.cost.totalAmount.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    totalTaxAmount = storeCart.cost.totalTaxAmount?.amount?.setScale(2, RoundingMode.HALF_UP)?.toPlainString(),
                    checkoutChargeAmount = storeCart.cost.checkoutChargeAmount.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    currencyCode = storeCart.cost.totalAmount.currencyCode
                )
            }
        }

        val colorScheme = MaterialTheme.colorScheme


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colorScheme.background.copy(alpha = 0.95f))
                .padding(16.dp)
        ) {
            val formattedTotal = "${storeCart.cost.totalAmount.currencyCode} ${storeCart.cost.totalAmount.amount.setScale(2, RoundingMode.HALF_UP).toPlainString()}"

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formattedTotal,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}