package com.example.qafilah.features.checkout.presentation.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.checkout.presentation.shared.CheckoutDisplayCart
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import com.example.ui_kit.components.checkout.CheckoutProductCard
import com.example.ui_kit.components.checkout.CheckoutSummaryCard
import com.example.ui_kit.components.checkout.DeliveryOptionsRadioGroup
import com.example.ui_kit.components.shared.PrimaryButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutSummaryScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: CheckoutSharedViewModel,
    summaryViewModel: CheckoutSummaryViewModel = koinViewModel(),
    onNavigateToPayment: () -> Unit
) {

    val cart by sharedViewModel.cartState.collectAsState()
    val displayCart by sharedViewModel.displayCartState.collectAsState()
    val localUiState by summaryViewModel.uiState.collectAsState()

    val selectedDeliveryHandle by sharedViewModel.selectedDeliveryHandle.collectAsState()

    LaunchedEffect(cart, selectedDeliveryHandle) {
        val safeCart = cart ?: return@LaunchedEffect

        if (selectedDeliveryHandle == null) {
            val firstGroup = safeCart.deliveryGroups.firstOrNull()
            val defaultOption = safeCart.defaultShippingOption ?: firstGroup?.deliveryOptions?.firstOrNull()

            if (firstGroup != null && defaultOption != null) {
                summaryViewModel.selectShippingOption(safeCart.id, firstGroup.id, defaultOption.handle) { updatedCart ->
                    sharedViewModel.updateCartStateWithSelectedShipping(updatedCart, defaultOption.handle)
                }
            }
        }
    }

    val finalUiState = buildSummaryUiState(
        cart = cart,
        isRecalculating = localUiState.isRecalculating,
        localSelectedHandle = localUiState.selectedDeliveryHandle
    )


    CheckoutSummaryContent(
        modifier = modifier,
        uiState = finalUiState,
        displayCart = displayCart,
        onShippingOptionSelected = { handle ->
            val groupId = cart!!.deliveryGroups.firstOrNull()?.id ?: return@CheckoutSummaryContent

            summaryViewModel.selectShippingOption(cart!!.id, groupId, handle) { updatedCart ->
                sharedViewModel.updateCartStateWithSelectedShipping(updatedCart, handle)
            }
        },
        onProceedToPayment = onNavigateToPayment
    )
}

@Composable
fun CheckoutSummaryContent(
    modifier: Modifier = Modifier,
    uiState: CheckoutSummaryUIState,
    displayCart: CheckoutDisplayCart?,
    onShippingOptionSelected: (String) -> Unit,
    onProceedToPayment: () -> Unit
) {
    val cart = uiState.cart ?: return
    val displayedCart = displayCart ?: return

    val uiKitDeliveryOptions = displayedCart.deliveryOptions.map { option ->
        Triple(option.handle, option.title, option.displayCost)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.checkout_order_summary_title),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(cart.lines) { lineItem ->
                val displayLine = displayedCart.lines.firstOrNull { it.id == lineItem.id } ?: return@items
                CheckoutProductCard(
                    imageUrl = lineItem.imageUrl ?: "",
                    title = displayLine.title,
                    variantTitle = displayLine.variantTitle,
                    price = displayLine.displayPrice,
                    quantityLabel = stringResource(R.string.checkout_quantity_label, lineItem.quantity)
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }

            if (uiKitDeliveryOptions.isNotEmpty()) {
                item {
                    DeliveryOptionsRadioGroup(
                        title = stringResource(R.string.checkout_shipping_method_title),
                        options = uiKitDeliveryOptions,
                        selectedOptionHandle = uiState.selectedDeliveryHandle,
                        onOptionSelected = onShippingOptionSelected
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }

            item {
                CheckoutSummaryCard(
                    subtotalLabel = stringResource(R.string.checkout_subtotal_label),
                    subtotal = displayedCart.displaySubtotal,
                    discountLabel = stringResource(R.string.checkout_discount_label),
                    discountAmount = displayedCart.displayDiscount,
                    shippingLabel = stringResource(R.string.checkout_shipping_label),
                    taxLabel = stringResource(R.string.checkout_tax_label),
                    totalLabel = stringResource(R.string.checkout_total_label),
                    tax = displayedCart.displayTax,
                    shippingAmount = displayedCart.displayShipping,
                    total = displayedCart.displayTotal,
                    isCalculating = uiState.isRecalculating
                )
            }
        }

        PrimaryButton(
            text = stringResource(R.string.checkout_proceed_to_payment),
            onClick = onProceedToPayment,
            enabled = uiState.selectedDeliveryHandle != null,
            isLoading = uiState.isRecalculating,
            modifier = Modifier.padding(top = 16.dp),
            trailingContent = {
                Text(
                    text = displayedCart.displayTotal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}


