package com.example.qafilah.features.cart.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.example.qafilah.R
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.contract.CartUIState
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import com.example.ui_kit.components.cart.AddDiscountDialog
import com.example.ui_kit.components.login.LoginPromptBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartOverlays(
    state: CartUIState,
    viewModel: CartViewModel,
    showDiscountDialog: Boolean,
    onDismissDiscountDialog: () -> Unit
) {
    if (state.showLoginPrompt) {
        LoginPromptBottomSheet(
            title = stringResource(R.string.login_prompt_title),
            subtitle = stringResource(R.string.login_prompt_subtitle, stringResource(R.string.feature_name_cart)),
            loginButtonLabel = stringResource(R.string.login_button_label),
            signUpButtonLabel = stringResource(R.string.signup_action_label),
            onDismiss = { viewModel.onIntent(CartIntent.DismissLoginPrompt) },
            onNavigateToLogin = { viewModel.onIntent(CartIntent.NavigateToLogin) },
            onNavigateToSignUp = { viewModel.onIntent(CartIntent.NavigateToSignUp) }
        )
    }

    if (showDiscountDialog) {
        AddDiscountDialog(
            inputValue = state.discountInput,
            onInputValueChange = { viewModel.onIntent(CartIntent.UpdateDiscountInput(it)) },
            title = stringResource(R.string.cart_add_discount_title),
            label = stringResource(R.string.cart_discount_code_label),
            applyButtonLabel = stringResource(R.string.cart_discount_apply),
            cancelButtonLabel = stringResource(R.string.cart_discount_cancel),
            isApplying = state.isApplyingDiscount,
            errorMessage = state.discountError,
            onApply = { viewModel.onIntent(CartIntent.ApplyDiscountCode) },
            onDismiss = onDismissDiscountDialog
        )
    }

    LaunchedEffect(state.isApplyingDiscount) {
        if (!state.isApplyingDiscount && state.discountError == null && state.discountInput.isEmpty()) {
            onDismissDiscountDialog()
        }
    }
}
