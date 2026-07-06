package com.example.qafilah.features.checkout.data.datasource

import com.example.qafilah.features.checkout.data.remote.paymob.PaymobApi
import com.example.qafilah.features.checkout.data.remote.paymob.PaymobBillingData
import com.example.qafilah.features.checkout.data.remote.paymob.PaymobIntentionRequest
import com.example.qafilah.features.checkout.data.remote.paymob.PaymobIntentionResponse
import com.example.qafilah.features.checkout.data.remote.paymob.PaymobItem

interface PaymentRemoteDataSource {
    suspend fun createIntention(
        amountInCents: Int,
        orderReference: String
    ): PaymobIntentionResponse
}

class PaymentRemoteDataSourceImpl(
    private val paymobApi: PaymobApi,
    private val secretKey: String,
    private val paymentMethodId: Int
) : PaymentRemoteDataSource {

    override suspend fun createIntention(
        amountInCents: Int,
        orderReference: String
    ): PaymobIntentionResponse {
        val uniqueReference = "$orderReference-${System.currentTimeMillis()}"

        val request = PaymobIntentionRequest(
            amount = amountInCents,
            payment_methods = listOf(paymentMethodId),
            items = listOf(
                PaymobItem(
                    name = "Order #${orderReference.takeLast(6)}",
                    amount = amountInCents
                )
            ),
            billing_data = PaymobBillingData(),
            special_reference = uniqueReference,
            extras = mapOf("cart_id" to orderReference)
        )

        return paymobApi.createIntention(
            token = "Token $secretKey",
            request = request
        )
    }
}