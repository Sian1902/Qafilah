package com.example.qafilah.features.profile.domain.repository

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.model.CustomerProfile

interface ProfileRepository {
    suspend fun getCustomerProfile(): Result<CustomerProfile>
    suspend fun getPersonalDetails(): Result<AppUser>
    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ): Result<AppUser>
}