package com.example.qafilah.features.checkout.presentation.payment

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.qafilah.core.util.LocalRealActivity
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import com.paymob.paymob_sdk.ui.PaymobSdkActivity
import com.paymob.paymob_sdk.ui.PaymobSdkListener

@Composable
fun CheckoutPaymentScreen(
    sharedViewModel: CheckoutSharedViewModel,
    paymentViewModel: CheckoutPaymentViewModel = koinViewModel(),
    onCheckoutComplete: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()
    val uiState by paymentViewModel.uiState.collectAsState()

    LaunchedEffect(cart) {
        paymentViewModel.updateCart(cart)
    }

    val currentCart = cart ?: return

    uiState.cardPaymentData?.let { data ->
        PaymobSdkLauncher(
            publicKey = data.publicKey,
            clientSecret = data.clientSecret,
            onResult = { success ->
                paymentViewModel.clearCardPaymentData()
                paymentViewModel.onPaymentComplete(success)
                sharedViewModel.updatePaymentStatus(success)
                if (success) onCheckoutComplete()
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
                    onCodSuccess = onCheckoutComplete
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

@Composable
private fun PaymentMethodCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                enabled = isEnabled,
                role = Role.RadioButton,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
            }

            RadioButton(
                selected = isSelected,
                enabled = isEnabled,
                onClick = null
            )
        }
    }
}

@Composable
private fun PaymobSdkLauncher(
    publicKey: String,
    clientSecret: String,
    onResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalRealActivity.current

    LaunchedEffect(publicKey, clientSecret) {
        var hasResolved = false

        try {
            PaymobSdkActivity.setPaymobSdkListener(object : PaymobSdkListener {
                override fun onSuccess(payResponse: HashMap<String, String?>) {
                    if (hasResolved) return
                    hasResolved = true
                    onResult(true)
                }

                override fun onFailure(msg: String?) {
                    if (hasResolved) return
                    hasResolved = true
                    onResult(false)
                }

                override fun onPending() {
                    if (hasResolved) return
                    hasResolved = true
                    onResult(false)
                }
            })

            val intent = Intent(activity, PaymobSdkActivity::class.java).apply {
                putExtra(PaymobSdkActivity.BundleKeys.PUBLIC_KEY, publicKey)
                putExtra(PaymobSdkActivity.BundleKeys.CLIENT_SECRET, clientSecret)
                putExtra(PaymobSdkActivity.BundleKeys.SHOW_RESULT_PAGE, true)
                putExtra(PaymobSdkActivity.BundleKeys.SHOW_TRANSACTION_RESULT, true)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            onResult(false)
        }
    }
}