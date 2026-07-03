package com.example.qafilah.features.address.data.repo

import com.example.qafilah.features.address.data.datasource.AddressRemoteDataSource
import com.example.qafilah.features.address.data.mapper.toData
import com.example.qafilah.features.address.data.mapper.toDomain
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val remoteDataSource: AddressRemoteDataSource
) : AddressRepository {

    override suspend fun getAddresses(accessToken: String): Result<List<ShippingAddress>> =
        withContext(Dispatchers.IO) {
            try {
                val addresses = remoteDataSource.getAddresses(accessToken).map { it.toDomain() }
                Result.success(addresses)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun createAddress(accessToken: String, address: ShippingAddress): Result<ShippingAddress> =
        withContext(Dispatchers.IO) {
            try {
                val createdAddress = remoteDataSource.createAddress(accessToken, address.toData())
                Result.success(createdAddress.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateAddress(accessToken: String, address: ShippingAddress): Result<ShippingAddress> =
        withContext(Dispatchers.IO) {
            try {
                val updatedAddress = remoteDataSource.updateAddress(accessToken, address.toData())
                Result.success(updatedAddress.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteAddress(accessToken: String, addressId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                remoteDataSource.deleteAddress(accessToken, addressId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun setDefaultAddress(accessToken: String, addressId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                remoteDataSource.setDefaultAddress(accessToken, addressId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
