package com.example.qafilah.features.profile.domain.repository

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.model.CustomerProfile

data class UpdateProfileRepositoryParams(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
)

interface ProfileRepository {
    suspend fun getCustomerProfile(): Result<CustomerProfile>
    suspend fun getPersonalDetails(): Result<AppUser>
    suspend fun updateProfile(params: UpdateProfileRepositoryParams): Result<AppUser>
}