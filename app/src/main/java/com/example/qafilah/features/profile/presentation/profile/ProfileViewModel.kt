package com.example.qafilah.features.profile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.core.token.TokenProvider
import com.example.qafilah.features.profile.domain.usecase.GetCustomerProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AddressOperationResult {
    data object Success : AddressOperationResult()
    data class Error(val message: String) : AddressOperationResult()
}

class ProfileViewModel(
    private val getCustomerProfile: GetCustomerProfileUseCase,
    private val tokenProvider: TokenProvider,
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            val token = tokenProvider.getToken()
            if (token == null) {
                _state.value = ProfileUiState.Error("Not authenticated")
                return@launch
            }

            getCustomerProfile(token)
                .onSuccess { profile ->
                    _state.value = ProfileUiState.Success(profile)
                }
                .onFailure { error ->
                    _state.value = ProfileUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

}
