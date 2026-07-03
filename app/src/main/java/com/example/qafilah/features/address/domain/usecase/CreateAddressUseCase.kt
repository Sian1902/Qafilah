package com.example.qafilah.features.address.domain.usecase

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.repository.AddressRepository

class CreateAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(accessToken: String, address: ShippingAddress): Result<ShippingAddress> =
        repository.createAddress(accessToken, address)
}
