package com.example.qafilah.features.checkout.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutPaymentScreen(
    sharedViewModel: CheckoutSharedViewModel,
    paymentViewModel: CheckoutPaymentViewModel = koinViewModel(),
    onCheckoutComplete: () -> Unit
) {
    val displayCart by sharedViewModel.displayCartState.collectAsState()

    val cart by sharedViewModel.cartState.collectAsState()
    val user by sharedViewModel.appUser.collectAsState()
    val address by sharedViewModel.selectedAddress.collectAsState()
    val deliveryHandle by sharedViewModel.selectedDeliveryHandle.collectAsState()

    val paymentState by paymentViewModel.uiState.collectAsState()

    val currentDisplayCart = displayCart ?: return

    LaunchedEffect(paymentState.successOrderId) {
        if (paymentState.successOrderId != null) {
            onCheckoutComplete()
        }
    }

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

        // Optional: Show error if the mutation fails
        paymentState.error?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (cart != null && user != null && address != null && deliveryHandle != null) {
                    paymentViewModel.finalizeOrder(
                        cart = cart!!,
                        user = user!!,
                        address = address!!,
                        selectedDeliveryHandle = deliveryHandle!!
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !paymentState.isProcessing
        ) {
            if (paymentState.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.checkout_submit_payment))
            }
        }
    }
}