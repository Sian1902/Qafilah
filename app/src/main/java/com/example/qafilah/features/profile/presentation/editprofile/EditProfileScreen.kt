package com.example.qafilah.features.profile.presentation.editprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.ui_kit.components.profile.ChangePhotoAvatar
import com.example.ui_kit.components.profile.DetailTopBar
import com.example.ui_kit.components.profile.EditableTextField
import com.example.ui_kit.components.profile.PrimaryButton
import com.example.ui_kit.components.profile.TextLinkButton
import com.example.ui_kit.theme.QafilahTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onChangePhotoClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            if (state.emailChanged) {
                snackbarHostState.showSnackbar(
                    message = "Please check your inbox to confirm your new email",
                    duration = SnackbarDuration.Indefinite,
                    actionLabel = "Dismiss"
                )
            } else {
                onBackClick()
            }
            viewModel.resetSaveSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
        ) {
            DetailTopBar(
                title = "Edit Profile",
                onBackClick = onBackClick,
                trailingIcon = Icons.Filled.Settings,
                onTrailingClick = onSettingsClick
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                EditProfileForm(
                    state = state,
                    onFirstNameChange = viewModel::onFirstNameChange,
                    onLastNameChange = viewModel::onLastNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onSaveClick = viewModel::onSaveClicked,
                    onCancelClick = onCancelClick,
                    onChangePhotoClick = onChangePhotoClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun EditProfileForm(
    state: EditProfileFormState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onChangePhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        ChangePhotoAvatar(
            onChangePhotoClick = onChangePhotoClick,
            avatarContent = {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )

        Spacer(Modifier.height(24.dp))

        if (state.generalError != null) {
            Text(
                text = state.generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EditableTextField(
                label = "First Name",
                value = state.firstName,
                onValueChange = onFirstNameChange,
                error = state.firstNameError
            )
            EditableTextField(
                label = "Last Name",
                value = state.lastName,
                onValueChange = onLastNameChange
            )
            EditableTextField(
                label = "Email Address",
                value = state.email,
                onValueChange = onEmailChange,
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                error = state.emailError
            )
            EditableTextField(
                label = "Phone Number",
                value = state.phone,
                onValueChange = onPhoneChange,
                leadingIcon = Icons.Filled.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                error = state.phoneError ?: if (state.phone.isNotEmpty() && !state.phone.startsWith(
                        "+"
                    )
                ) "Start with + and country code" else null
            )
        }

        Spacer(Modifier.height(28.dp))

        PrimaryButton(
            text = if (state.isSaving) "Saving..." else "Save Changes",
            onClick = onSaveClick,
            enabled = !state.isSaving
        )

        Spacer(Modifier.height(8.dp))

        TextLinkButton(
            text = "Cancel",
            onClick = onCancelClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    QafilahTheme {
        EditProfileScreen()
    }
}
