package com.example.qafilah.core.currency.domain.usecase

import com.example.qafilah.core.currency.domain.repo.CurrencyRepository

class ConvertRawPriceUseCase(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(amountUsd: Double, targetCurrency: String): Double {
        val rates = currencyRepository.getExchangeRates()
        val rate = rates[targetCurrency] ?: return amountUsd
        return amountUsd * rate
    }
}