package com.example.qafilah.features.checkout.data.remote.paymob

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymobApi {
    @Headers("Content-Type: application/json")
    @POST("v1/intention/")
    suspend fun createIntention(
        @Header("Authorization") token: String,
        @Body request: PaymobIntentionRequest
    ): PaymobIntentionResponse
}

data class PaymobIntentionRequest(
    val amount: Int,
    val currency: String = "EGP",
    val payment_methods: List<Int>,
    val items: List<PaymobItem>,
    val billing_data: PaymobBillingData,
    val special_reference: String,
    val extras: Map<String, String> = emptyMap()
)

data class PaymobItem(
    val name: String,
    val amount: Int,
    val description: String = "Order payment",
    val quantity: Int = 1
)

data class PaymobBillingData(
    val first_name: String = "Test",
    val last_name: String = "User",
    val email: String = "test@example.com",
    val phone_number: String = "+201234567890",
    val city: String = "Cairo",
    val country: String = "EG",
    val street: String = "NA",
    val building: String = "NA",
    val floor: String = "NA",
    val apartment: String = "NA"
)

data class PaymobIntentionResponse(
    val client_secret: String
)