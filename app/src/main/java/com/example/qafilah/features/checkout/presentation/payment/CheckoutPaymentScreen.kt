package com.example.qafilah.features.checkout.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel

@Composable
fun CheckoutPaymentScreen(
    sharedViewModel: CheckoutSharedViewModel,
    onCheckoutComplete: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()
    val displayCart by sharedViewModel.displayCartState.collectAsState()

    cart ?: return
    val currentDisplayCart = displayCart ?: return

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.checkout_step_payment_title), style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(
                R.string.checkout_amount_to_charge,
                currentDisplayCart.displayTotal
            ),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onCheckoutComplete, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.checkout_submit_payment))
        }
    }
}