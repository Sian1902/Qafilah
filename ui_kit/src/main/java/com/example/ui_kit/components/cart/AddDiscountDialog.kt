package com.example.ui_kit.components.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddDiscountDialog(
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    isApplying: Boolean,
    errorMessage: String?,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = {
            if (!isApplying) onDismiss()
        },
        containerColor = colorScheme.surface,
        titleContentColor = colorScheme.onBackground,
        textContentColor = colorScheme.onBackground,
        title = {
            Text(
                text = "Add Promo Code",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = onInputValueChange,
                    label = {
                        Text(
                            text = "Code",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    enabled = !isApplying,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        cursorColor = colorScheme.primary
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onApply,
                enabled = inputValue.isNotBlank() && !isApplying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) {
                if (isApplying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Apply",
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isApplying
            ) {
                Text(
                    text = "Cancel",
                    color = colorScheme.onBackground
                )
            }
        }
    )
}