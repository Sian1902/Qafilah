package com.example.ui_kit.components.auth

import android.util.Patterns
import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui_kit.components.shared.PrimaryButton
import com.example.ui_kit.theme.QafilahTheme

@Composable
fun LoginCard(
    modifier: Modifier = Modifier,
    onLogin: (email: String, password: String) -> Unit,
    authErrorMessage: String? = null
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var emailHasError by rememberSaveable { mutableStateOf(false) }
    var passwordHasError by rememberSaveable { mutableStateOf(false) }

    val validateAndSubmit = {
        emailHasError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        passwordHasError = password.isBlank()

        if (!emailHasError && !passwordHasError) {
            onLogin(email, password)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            if (authErrorMessage != null) {
                Text(
                    text = authErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            EmailInputField(
                value = email,
                onValueChange = {
                    email = it
                    emailHasError = false
                },
                isError = emailHasError
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordInputField(
                value = password,
                onValueChange = {
                    password = it
                    passwordHasError = false
                },
                isError = passwordHasError,
                onForgotPasswordClick = { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Login",
                onClick = validateAndSubmit,
            )


            Spacer(modifier = Modifier.height(32.dp))

            SocialLoginSection()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginCardPreview() {
    QafilahTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LoginCard(
                onLogin = { _, _ -> }
            )
        }
    }
}