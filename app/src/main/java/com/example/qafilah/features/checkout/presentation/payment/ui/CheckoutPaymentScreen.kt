package com.example.qafilah.features.checkout.presentation.payment.ui

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
import com.example.ui_kit.components.shared.PrimaryButton
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment

@Composable
fun CheckoutPaymentScreen(
    sharedViewModel: CheckoutSharedViewModel,
    paymentViewModel: CheckoutPaymentViewModel = koinViewModel(),
    onNavigateToSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by paymentViewModel.uiState.collectAsState()

    val cart by sharedViewModel.cartState.collectAsState()
    val displayCart by sharedViewModel.displayCartState.collectAsState()
    val user by sharedViewModel.appUser.collectAsState()
    val address by sharedViewModel.selectedAddress.collectAsState()
    val deliveryHandle by sharedViewModel.selectedDeliveryHandle.collectAsState()

    LaunchedEffect(cart) {
        paymentViewModel.updateCart(cart)
    }

    LaunchedEffect(uiState.successOrderId, uiState.error) {
        if (uiState.successOrderId != null) {
            onNavigateToSuccess()
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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.checkout_step_payment_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        val displayTotal = displayCart?.displayTotal ?: ""

        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(

                    text = "Total due",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = displayTotal,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.checkout_payment_method),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaymentMethodCard(
                icon = Icons.Filled.CreditCard,
                title = stringResource(R.string.checkout_credit_debit_card),
                subtitle = stringResource(R.string.checkout_pay_securely_via_paymob),
                isSelected = uiState.selectedMethod == PaymentMethod.CARD,
                isEnabled = true,
                onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.CARD) }
            )

            PaymentMethodCard(
                icon = Icons.Filled.LocalShipping,
                title = stringResource(R.string.checkout_cash_on_delivery),
                subtitle = if (uiState.isCodAvailable) {
                    stringResource(R.string.checkout_pay_on_arrival)
                } else {
                    stringResource(R.string.checkout_cod_limit_warning, uiState.codLimit.toInt())
                },
                isSelected = uiState.selectedMethod == PaymentMethod.COD,
                isEnabled = uiState.isCodAvailable,
                onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.COD) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val buttonText = if (uiState.selectedMethod == PaymentMethod.COD) {
            stringResource(R.string.checkout_place_order)
        } else {
            stringResource(R.string.checkout_pay_now)
        }

        PrimaryButton(
            text = buttonText,
            onClick = {
                paymentViewModel.initiatePayment(
                    cart = currentCart,
                    onCodSuccess = {
                        triggerFinalizeOrder()
                    }
                )
            },
            isLoading = uiState.isProcessing
        )
    }
}
