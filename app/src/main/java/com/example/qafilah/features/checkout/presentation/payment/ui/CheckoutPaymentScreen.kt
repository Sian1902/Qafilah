package com.example.qafilah.features.checkout.presentation.payment.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.features.checkout.presentation.payment.CheckoutPaymentViewModel
import com.example.qafilah.features.checkout.presentation.payment.PaymentMethod
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutPaymentScreen(
    sharedViewModel: CheckoutSharedViewModel,
    paymentViewModel: CheckoutPaymentViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val uiState by paymentViewModel.uiState.collectAsState()

    val cart by sharedViewModel.cartState.collectAsState()
    val user by sharedViewModel.appUser.collectAsState()
    val address by sharedViewModel.selectedAddress.collectAsState()
    val deliveryHandle by sharedViewModel.selectedDeliveryHandle.collectAsState()

    LaunchedEffect(cart) {
        paymentViewModel.updateCart(cart)
    }

    LaunchedEffect(uiState.successOrderId, uiState.error) {
        if (uiState.successOrderId != null) {
            Toast.makeText(context, "Congratulations! Order Placed Successfully.", Toast.LENGTH_LONG).show()
            onNavigateToHome()
        }
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
        }
    }

    val currentCart = cart ?: return

    val triggerFinalizeOrder = {
        if (user != null && address != null && deliveryHandle != null) {
            paymentViewModel.finalizeOrder(
                cart = currentCart,
                user = user!!,
                address = address!!,
                selectedDeliveryHandle = deliveryHandle!!
            )
        }
    }

    uiState.cardPaymentData?.let { data ->
        PaymobSdkLauncher(
            publicKey = data.publicKey,
            clientSecret = data.clientSecret,
            onResult = { success, errorMessage ->
                paymentViewModel.clearCardPaymentData()
                paymentViewModel.onPaymentComplete(success, errorMessage)
                sharedViewModel.updatePaymentStatus(success)

                if (success) {
                    triggerFinalizeOrder()
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.checkout_step_payment_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        val formattedTotal = NumberFormat.getCurrencyInstance(Locale.US)
            .format(currentCart.cost.totalAmount.amount)
        Text(
            text = "Total due $formattedTotal",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Payment method",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaymentMethodCard(
                icon = Icons.Filled.CreditCard,
                title = "Credit / Debit Card",
                subtitle = "Pay securely via Paymob",
                isSelected = uiState.selectedMethod == PaymentMethod.CARD,
                isEnabled = true,
                onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.CARD) }
            )

            PaymentMethodCard(
                icon = Icons.Filled.LocalShipping,
                title = "Cash on Delivery",
                subtitle = if (uiState.isCodAvailable) {
                    "Pay when your order arrives"
                } else {
                    "Only available for orders under ${uiState.codLimit.toInt()} EGP"
                },
                isSelected = uiState.selectedMethod == PaymentMethod.COD,
                isEnabled = uiState.isCodAvailable,
                onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.COD) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val buttonText = if (uiState.selectedMethod == PaymentMethod.COD) {
            "Place order"
        } else {
            "Pay now"
        }

        Button(
            onClick = {
                paymentViewModel.initiatePayment(
                    cart = currentCart,
                    onCodSuccess = {
                        triggerFinalizeOrder()
                    }
                )
            },
            enabled = !uiState.isProcessing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (uiState.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(buttonText, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
