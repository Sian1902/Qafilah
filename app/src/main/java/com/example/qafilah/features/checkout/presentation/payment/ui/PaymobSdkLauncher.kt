package com.example.qafilah.features.checkout.presentation.payment.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.example.qafilah.R
import com.example.qafilah.core.util.LocalRealActivity
import com.paymob.paymob_sdk.ui.PaymobSdkActivity
import com.paymob.paymob_sdk.ui.PaymobSdkListener

@Composable
fun PaymobSdkLauncher(
    publicKey: String,
    clientSecret: String,
    onResult: (Boolean, String?) -> Unit
) {
    val activity = LocalRealActivity.current

    val pendingMessage = stringResource(R.string.payment_pending_confirmation)
    val errorPrefix = stringResource(R.string.payment_error_prefix)
    val sdkLaunchFailedTemplate = stringResource(R.string.payment_sdk_launch_failed)

    LaunchedEffect(publicKey, clientSecret) {
        var hasResolved = false

        try {
            PaymobSdkActivity.setPaymobSdkListener(object : PaymobSdkListener {
                override fun onSuccess(payResponse: HashMap<String, String?>) {
                    if (hasResolved) return
                    hasResolved = true
                    onResult(true, null)
                }

                override fun onFailure(msg: String?) {
                    if (hasResolved) return
                    hasResolved = true

                    // TODO: Assure Payment
                    if (msg == null || msg.contains("cancel", ignoreCase = true)) {
                        onResult(true, null)
                    } else {
                        onResult(false, errorPrefix.format(msg))
                    }
                }

                override fun onPending() {
                    if (hasResolved) return
                    hasResolved = true
                    onResult(false, pendingMessage)
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
            onResult(false, sdkLaunchFailedTemplate.format(e.message.orEmpty()))
        }
    }
}