package com.example.qafilah.features.address.domain.usecase

import com.example.qafilah.features.address.domain.model.AddressSuggestion
import com.example.qafilah.features.address.domain.repository.AddressRepository

class SearchAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(query: String): Result<List<AddressSuggestion>> {
        if (query.length < 3) return Result.success(emptyList())
        return repository.getAddressSuggestions(query)
    }
}