package com.example.qafilah.features.checkout.presentation.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutSummaryScreen(
    sharedViewModel: CheckoutSharedViewModel,
    summaryViewModel: CheckoutSummaryViewModel = koinViewModel(),
    onNavigateToPayment: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()
    val isUpdatingShipping by summaryViewModel.isShippingUpdating.collectAsState()

    val selectedHandle by summaryViewModel.selectedShippingHandle.collectAsState(
        initial = cart.defaultShippingOption?.handle
    )

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Step 2: Order Summary", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
            items(cart.lines) { lineItem ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(lineItem.productTitle, style = MaterialTheme.typography.bodyLarge)
                        Text("Qty: ${lineItem.quantity} | ${lineItem.variantTitle}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("$${lineItem.price.amount}")
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                cart.deliveryGroups.firstOrNull()?.let { group ->
                    Text("Shipping Methods", style = MaterialTheme.typography.titleMedium)
                    group.deliveryOptions.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedHandle == option.handle,
                                onClick = {
                                    summaryViewModel.selectShippingOption(cart.id, group.id, option.handle) { updatedCart ->
                                        sharedViewModel.updateCartState(updatedCart)
                                    }
                                }
                            )
                            Text("${option.title} - $${option.estimatedCost.amount}")
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                if (isUpdatingShipping) {
                    Text("Recalculating...")
                } else {
                    Text("Subtotal: $${cart.cost.subtotalAmount.amount}")
                    Text("Tax: $${cart.cost.totalTaxAmount?.amount ?: "0.00"}")
                    Text("Total: $${cart.cost.totalAmount.amount}", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Button(
            onClick = onNavigateToPayment,
            enabled = !isUpdatingShipping,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed to Payment")
        }
    }
}