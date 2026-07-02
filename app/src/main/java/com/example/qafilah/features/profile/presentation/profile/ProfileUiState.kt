package com.example.qafilah.features.profile.presentation.profile

import com.example.qafilah.features.profile.domain.model.CustomerProfile

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val profile: CustomerProfile) : ProfileUiState()
    data class Error(val message: String?) : ProfileUiState()
}