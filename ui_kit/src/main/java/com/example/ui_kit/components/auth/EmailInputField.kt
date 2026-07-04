package com.example.ui_kit.components.auth

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EmailInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    emailLabel: String,
    emailPlaceholder: String,
    emailErrorMessage: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        label = emailLabel,
        placeholder = emailPlaceholder,
        errorMessage = emailErrorMessage,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}