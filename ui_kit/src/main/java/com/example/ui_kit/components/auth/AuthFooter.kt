package com.example.ui_kit.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui_kit.theme.QafilahTheme

@Composable
fun AuthFooter(
    modifier: Modifier = Modifier,
    isInLogin: Boolean = true,
    loginPrompt: String,
    loginActionLabel: String,
    signupPrompt: String,
    signupActionLabel: String,
    guestButtonLabel: String,
    guestButtonContentDescription: String,
    onLoginAsGuest: () -> Unit,
    onNavigate: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isInLogin) loginPrompt else signupPrompt,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            TextButton(
                onClick = onNavigate,
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(
                    text = if (isInLogin) loginActionLabel else signupActionLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLoginAsGuest,
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = guestButtonContentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = guestButtonLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AuthFooterPreview() {
    QafilahTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
 //            AuthFooter(
//                onLoginAsGuest = {},
//                onNavigate = {}
//            )
        }
    }
}