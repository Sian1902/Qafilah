package com.example.qafilah.features.address.domain.usecase

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.repository.AddressRepository

class GetAddressesUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(accessToken: String): Result<List<ShippingAddress>> =
        repository.getAddresses(accessToken)
}
