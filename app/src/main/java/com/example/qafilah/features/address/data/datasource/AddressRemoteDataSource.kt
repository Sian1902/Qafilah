package com.example.qafilah.features.address.data.datasource

import com.example.qafilah.features.address.domain.model.CustomerAddress

interface AddressRemoteDataSource {
    suspend fun getAddresses(accessToken: String): List<CustomerAddress>
    suspend fun createAddress(accessToken: String, address: CustomerAddress): CustomerAddress
    suspend fun updateAddress(accessToken: String, address: CustomerAddress): CustomerAddress
    suspend fun deleteAddress(accessToken: String, addressId: String): Unit
}
