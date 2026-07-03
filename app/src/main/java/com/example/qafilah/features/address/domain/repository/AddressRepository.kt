package com.example.qafilah.features.address.domain.repository

import com.example.qafilah.features.address.domain.model.ShippingAddress

interface AddressRepository {
    suspend fun getAddresses(accessToken: String): Result<List<ShippingAddress>>
    suspend fun createAddress(accessToken: String, address: ShippingAddress): Result<ShippingAddress>
    suspend fun updateAddress(accessToken: String, address: ShippingAddress): Result<ShippingAddress>
    suspend fun deleteAddress(accessToken: String, addressId: String): Result<Unit>
    suspend fun setDefaultAddress(accessToken: String, addressId: String): Result<Unit>
}
