package com.example.qafilah.core.currency

import kotlinx.coroutines.flow.first
import java.util.Locale

class ConvertPriceUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(amountUsd: Double): String {
        val targetCurrency = repository.getSelectedCurrency().first()
        val rates = repository.getExchangeRates()
        val rate = rates[targetCurrency] ?: 1.0

        val convertedAmount = amountUsd * rate

        return String.format(Locale.getDefault(), "%.2f %s", convertedAmount, targetCurrency)
    }
}
