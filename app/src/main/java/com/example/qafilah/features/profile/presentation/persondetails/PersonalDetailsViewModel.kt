package com.example.qafilah.features.profile.presentation.persondetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.usecase.GetPersonalDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PersonalDetailsUiState {
    data object Loading : PersonalDetailsUiState()
    data class Success(val user: AppUser) : PersonalDetailsUiState()
    data class Error(val message: String) : PersonalDetailsUiState()
}

class PersonalDetailsViewModel(
    private val getPersonalDetailsUseCase: GetPersonalDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonalDetailsUiState>(PersonalDetailsUiState.Loading)
    val uiState: StateFlow<PersonalDetailsUiState> = _uiState.asStateFlow()

    private var unknownErrorMessage: String = ""

    fun setLocalizedStrings(unknownError: String) {
        unknownErrorMessage = unknownError
    }

    init {
        loadPersonalDetails()
    }

    fun loadPersonalDetails() {
        viewModelScope.launch {
            _uiState.value = PersonalDetailsUiState.Loading
            
            getPersonalDetailsUseCase().fold(
                onSuccess = { user ->
                    _uiState.value = PersonalDetailsUiState.Success(user)
                },
                onFailure = { error ->
                    _uiState.value = PersonalDetailsUiState.Error(error.message ?: unknownErrorMessage)
                }
            )
        }
    }
}
