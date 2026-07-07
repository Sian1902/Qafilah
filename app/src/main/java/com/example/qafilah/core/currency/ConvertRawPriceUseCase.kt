package com.example.qafilah.core.currency

import com.example.qafilah.core.currency.domain.repo.CurrencyRepository

class ConvertRawPriceUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(amountUsd: Double, targetCurrency: String = "EGP"): Double {
        val rates = repository.getExchangeRates()
        val rate = rates[targetCurrency] ?: 1.0

        return amountUsd * rate
    }
}