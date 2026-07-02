package com.example.qafilah.features.profile.data.repo

import com.apollographql.apollo.api.Optional
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.auth.domain.repository.AuthRepository
import com.example.qafilah.features.profile.data.datasource.ProfileRemoteDataSource
import com.example.qafilah.features.profile.data.mapper.toDomain
import com.example.qafilah.features.profile.domain.model.CustomerProfile
import com.example.qafilah.features.profile.domain.repository.ProfileRepository
import com.example.qafilah.graphql.storefront.type.CustomerUpdateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val authRepository: AuthRepository
) : ProfileRepository {

    override suspend fun getCustomerProfile(accessToken: String): Result<CustomerProfile> =
        withContext(Dispatchers.IO) {
            try {
                val customer = remoteDataSource.getCustomerProfile(accessToken)
                    ?: return@withContext Result.failure(Exception("Customer not found"))

                val firebaseUid = authRepository.getCurrentUser()?.id ?: ""
                Result.success(customer.toDomain(firebaseUid))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getPersonalDetails(accessToken: String): Result<AppUser> =
        withContext(Dispatchers.IO) {
            try {
                val customer = remoteDataSource.getPersonalDetails(accessToken)
                    ?: return@withContext Result.failure(Exception("Customer not found"))

                Result.success(
                    AppUser(
                        id = customer.id,
                        firstName = customer.firstName,
                        lastName = customer.lastName,
                        email = customer.email,
                        phone = customer.phone
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateProfile(
        accessToken: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ): Result<AppUser> = withContext(Dispatchers.IO) {
        try {
            val input = CustomerUpdateInput(
                firstName = Optional.present(firstName),
                lastName = Optional.present(lastName),
                email = Optional.present(email),
                phone = Optional.present(phone)
            )
            val updateResult = remoteDataSource.updateCustomer(accessToken, input)
                ?: return@withContext Result.failure(Exception("Update failed: empty response"))

            if (!updateResult.customerUserErrors.isNullOrEmpty()) {
                val errorMsg = updateResult.customerUserErrors.joinToString { it.message }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val updatedCustomer = updateResult.customer
                ?: return@withContext Result.failure(Exception("Update failed: no customer data"))

            Result.success(
                AppUser(
                    id = updatedCustomer.id,
                    firstName = updatedCustomer.firstName,
                    lastName = updatedCustomer.lastName,
                    email = updatedCustomer.email,
                    phone = updatedCustomer.phone
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
