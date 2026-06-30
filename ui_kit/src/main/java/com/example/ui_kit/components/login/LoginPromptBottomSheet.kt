package com.example.ui_kit.components.login


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet shown when a guest taps Cart or Wishlist.
 *
 * Purely presentational — no ViewModel or business logic inside.
 * The parent ViewModel drives [showLoginPrompt] via MVI state and
 * passes navigation callbacks down through the Screen composable.
 *
 * ### Wiring example (inside CartScreen / WishlistScreen)
 * ```kotlin
 * val state by viewModel.state.collectAsStateWithLifecycle()
 *
 * if (state.showLoginPrompt) {
 *     LoginPromptBottomSheet(
 *         featureName        = "your cart",
 *         onDismiss          = { viewModel.onIntent(CartIntent.DismissLoginPrompt) },
 *         onNavigateToLogin  = { viewModel.onIntent(CartIntent.NavigateToLogin) },
 *         onNavigateToSignUp = { viewModel.onIntent(CartIntent.NavigateToSignUp) }
 *     )
 * }
 * ```
 *
 * @param featureName        Used in the subtitle — e.g. "your cart", "your wishlist".
 * @param onDismiss          Fires when the user swipes away or taps the scrim.
 * @param onNavigateToLogin  Fires when the user taps **Login**.
 * @param onNavigateToSignUp Fires when the user taps **Create an account**.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPromptBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
    featureName: String = "this feature",
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "You need an account to access $featureName.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { onDismiss(); onNavigateToLogin() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onDismiss(); onNavigateToSignUp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create an account")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}