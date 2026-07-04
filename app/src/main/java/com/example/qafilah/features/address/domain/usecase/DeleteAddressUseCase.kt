package com.example.qafilah.features.address.domain.usecase

import com.example.qafilah.features.address.domain.repository.AddressRepository

class DeleteAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(accessToken: String, addressId: String): Result<Unit> =
        repository.deleteAddress(accessToken, addressId)
}
