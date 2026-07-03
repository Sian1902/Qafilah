package com.example.qafilah.features.address.domain.usecase

import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.repository.AddressRepository

class UpdateAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(accessToken: String, address: ShippingAddress): Result<ShippingAddress> =
        repository.updateAddress(accessToken, address)
}
