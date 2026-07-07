package com.example.qafilah.features.profile.presentation.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.profile.domain.usecase.GetPersonalDetailsUseCase
import com.example.qafilah.features.profile.domain.usecase.UpdateProfileParams
import com.example.qafilah.features.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val firstNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val generalError: String? = null,
    val saveSuccess: Boolean = false,
    val emailChanged: Boolean = false
)

class EditProfileViewModel(
    private val getPersonalDetailsUseCase: GetPersonalDetailsUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileFormState())
    val state: StateFlow<EditProfileFormState> = _state.asStateFlow()

    private var originalUser: AppUser? = null

    private var updateFailedMessage: String = ""
    private var phoneFormatHint: String = ""
    private var firstNameEmptyMessage: String = ""
    private var invalidEmailMessage: String = ""
    private var phoneEmptyMessage: String = ""

    fun setLocalizedStrings(
        updateFailed: String,
        phoneFormat: String,
        firstNameEmpty: String,
        invalidEmail: String,
        phoneEmpty: String
    ) {
        updateFailedMessage = updateFailed
        phoneFormatHint = phoneFormat
        firstNameEmptyMessage = firstNameEmpty
        invalidEmailMessage = invalidEmail
        phoneEmptyMessage = phoneEmpty
    }

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            getPersonalDetailsUseCase().fold(
                onSuccess = { user ->
                    originalUser = user
                    _state.update {
                        it.copy(
                            isLoading = false,
                            firstName = user.firstName ?: "",
                            lastName = user.lastName ?: "",
                            email = user.email ?: "",
                            phone = user.phone ?: ""
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, generalError = error.message) }
                }
            )
        }
    }

    fun onFirstNameChange(newValue: String) {
        _state.update { it.copy(firstName = newValue, firstNameError = null) }
    }

    fun onLastNameChange(newValue: String) {
        _state.update { it.copy(lastName = newValue) }
    }

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue, emailError = null) }
    }

    fun onPhoneChange(newValue: String) {
        _state.update { it.copy(phone = newValue, phoneError = null) }
    }

    fun onSaveClicked() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, generalError = null) }
            
            updateProfileUseCase(
                UpdateProfileParams(
                    firstName = currentState.firstName,
                    lastName = currentState.lastName,
                    email = currentState.email,
                    phone = currentState.phone,
                    originalEmail = originalUser?.email
                )
            ).fold(
                onSuccess = { (updatedUser, emailChanged) ->
                    originalUser = updatedUser
                    _state.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            emailChanged = emailChanged,
                            firstName = updatedUser.firstName ?: "",
                            lastName = updatedUser.lastName ?: "",
                            email = updatedUser.email ?: "",
                            phone = updatedUser.phone ?: ""
                        )
                    }
                },
                onFailure = { error ->
                    handleUpdateError(error)
                }
            )
        }
    }

    private fun handleUpdateError(error: Throwable) {
        _state.update { it.copy(isSaving = false) }

        when (error) {
            is com.example.qafilah.features.profile.domain.usecase.UpdateProfileError.FirstNameEmpty ->
                _state.update { it.copy(firstNameError = firstNameEmptyMessage) }

            is com.example.qafilah.features.profile.domain.usecase.UpdateProfileError.InvalidEmail ->
                _state.update { it.copy(emailError = invalidEmailMessage) }

            is com.example.qafilah.features.profile.domain.usecase.UpdateProfileError.PhoneEmpty ->
                _state.update { it.copy(phoneError = phoneEmptyMessage) }

            is com.example.qafilah.features.profile.domain.usecase.UpdateProfileError.InvalidPhoneFormat ->
                _state.update { it.copy(phoneError = phoneFormatHint) }

            else -> {
                val message = error.message ?: updateFailedMessage
                _state.update { it.copy(generalError = message) }
            }
        }
    }

    fun resetSaveSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }
}
