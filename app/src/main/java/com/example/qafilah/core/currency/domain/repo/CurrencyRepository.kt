package com.example.qafilah.core.currency.domain.repo

import com.example.qafilah.BuildConfig
import com.example.qafilah.core.currency.data.remote.CurrencyService
import com.example.qafilah.core.currency.domain.model.CurrencyMetadata
import com.example.qafilah.core.preferences.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface CurrencyRepository {
    fun getSelectedCurrency(): Flow<String>
    suspend fun setSelectedCurrency(code: String)
    suspend fun getExchangeRates(): Map<String, Double>
    suspend fun getSupportedCurrencies(): List<CurrencyMetadata>
}

class CurrencyRepositoryImpl(
    private val apiService: CurrencyService,
    private val preferences: AppPreferences
) : CurrencyRepository {

    private val mutex = Mutex()
    private var cachedRates: Map<String, Double>? = null
    private var cachedMetadata: List<CurrencyMetadata>? = null

    private val apiKey = BuildConfig.EXCHANGE_RATE_API_KEY

    override fun getSelectedCurrency(): Flow<String> = preferences.currencyCode

    override suspend fun setSelectedCurrency(code: String) {
        preferences.setCurrencyCode(code)
    }

    override suspend fun getExchangeRates(): Map<String, Double> {
        return mutex.withLock {
            cachedRates ?: try {
                val response = apiService.getLatestRates(apiKey, "USD")
                if (response.result == "success") {
                    response.conversionRates.also { cachedRates = it }
                } else {
                    getFallbackRates()
                }
            } catch (e: Exception) {
                getFallbackRates()
            }
        }
    }

    override suspend fun getSupportedCurrencies(): List<CurrencyMetadata> {
        return mutex.withLock {
            cachedMetadata ?: try {
                val response = apiService.getSupportedCodes(apiKey)
                if (response.result == "success") {
                    response.supportedCodes.map {
                        CurrencyMetadata(code = it[0], fullName = it[1])
                    }.also { cachedMetadata = it }
                } else {
                    getFallbackMetadata()
                }
            } catch (e: Exception) {
                getFallbackMetadata()
            }
        }
    }

    private fun getFallbackRates() = mapOf(
        "USD" to 1.0,
        "EGP" to 48.45,
        "AED" to 3.67,
        "SAR" to 3.75,
        "EUR" to 0.92,
        "GBP" to 0.77
    )

    private fun getFallbackMetadata() = listOf(
        CurrencyMetadata("USD", "United States Dollar"),
        CurrencyMetadata("EGP", "Egyptian Pound"),
        CurrencyMetadata("AED", "UAE Dirham"),
        CurrencyMetadata("SAR", "Saudi Riyal"),
        CurrencyMetadata("EUR", "Euro"),
        CurrencyMetadata("GBP", "British Pound")
    )
}
