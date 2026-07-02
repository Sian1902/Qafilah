package com.example.qafilah.features.profile.domain.repository

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.model.CustomerProfile

interface ProfileRepository {
    suspend fun getCustomerProfile(accessToken: String): Result<CustomerProfile>
    suspend fun getPersonalDetails(accessToken: String): Result<AppUser>
    suspend fun updateProfile(
        accessToken: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ): Result<AppUser>

}