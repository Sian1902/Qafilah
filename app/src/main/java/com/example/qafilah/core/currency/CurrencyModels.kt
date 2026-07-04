package com.example.qafilah.core.currency

import com.google.gson.annotations.SerializedName

data class CurrencyMetadata(
    val code: String,
    val fullName: String
)

data class CurrencyRateResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("conversion_rates") val conversionRates: Map<String, Double>
)

data class CurrencyCodesResponse(
    @SerializedName("result") val result: String,
    @SerializedName("supported_codes") val supportedCodes: List<List<String>>
)
