package com.example.qafilah.features.checkout.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutPaymentScreen(
    sharedViewModel: CheckoutSharedViewModel,
    paymentViewModel: CheckoutPaymentViewModel = koinViewModel(),
    onCheckoutComplete: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Step 3: Payment", style = MaterialTheme.typography.headlineMedium)
        Text("Amount to charge: $${cart!!.cost.totalAmount.amount}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onCheckoutComplete, modifier = Modifier.fillMaxWidth()) {
            Text("Submit Payment")
        }
    }
}