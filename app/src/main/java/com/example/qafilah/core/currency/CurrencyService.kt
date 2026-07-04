package com.example.qafilah.core.currency

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyService {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String = "USD"
    ): CurrencyRateResponse

    @GET("v6/{apiKey}/codes")
    suspend fun getSupportedCodes(
        @Path("apiKey") apiKey: String
    ): CurrencyCodesResponse
}
