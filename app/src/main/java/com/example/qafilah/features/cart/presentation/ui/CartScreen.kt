package com.example.qafilah.features.cart.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.R
import com.example.qafilah.features.cart.presentation.contract.CartEvent
import com.example.qafilah.features.cart.presentation.contract.CartIntent
import com.example.qafilah.features.cart.presentation.contract.CartUiModel
import com.example.qafilah.features.cart.presentation.viewmodel.CartViewModel
import com.example.ui_kit.components.cart.CartEmptyView
import com.example.ui_kit.components.cart.CartItemCard
import com.example.ui_kit.components.cart.CartSummaryCard
import com.example.ui_kit.components.login.LoginPromptBottomSheet
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {},
    viewModel: CartViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val errorFailedToLoadCart = stringResource(R.string.error_failed_to_load_cart)

    LaunchedEffect(Unit) {
        viewModel.onIntent(CartIntent.EnterScreen(fallbackErrorMessage = errorFailedToLoadCart))
        viewModel.events.collect { event ->
            when (event) {
                CartEvent.NavigateToLogin -> onNavigateToLogin()
                CartEvent.NavigateToSignUp -> onNavigateToSignUp()
                CartEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(CartIntent.DismissError)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        state.cart?.let { cart ->
            CartContent(
                modifier = Modifier.fillMaxSize(),
                cart = cart,
                onIncreaseQuantity = { lineId ->
                    viewModel.onIntent(CartIntent.IncreaseQuantity(lineId))
                },
                onDecreaseQuantity = { lineId ->
                    viewModel.onIntent(CartIntent.DecreaseQuantity(lineId))
                },
                onRemoveItem = { lineId ->
                    viewModel.onIntent(CartIntent.RemoveItem(lineId))
                },
                onCheckout = onNavigateToCheckout
            )
        }

        if (state.isLoading && state.cart == null) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp
            )
        }
    }

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
}

@Composable
private fun CartContent(
    modifier: Modifier = Modifier,
    cart: CartUiModel,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit
) {
    if (cart.lines.isEmpty()) {
        CartEmptyView(
            modifier = modifier.padding(16.dp),
            emptyCartMessage = stringResource(R.string.empty_cart_message),
            emptyCartContentDescription = stringResource(R.string.empty_cart_content_description),
            exploreMessage = stringResource(R.string.explore_message)
        )
    } else {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cart.lines, key = { it.id }) { line ->
                    CartItemCard(
                        imageUrl = line.imageUrl.orEmpty(),
                        title = line.title,
                        price = line.displayTotal,
                        quantity = line.quantity,
                        quantityAvailable = 100,
                        removeConfirmationTitle = stringResource(R.string.cart_remove_item_title),
                        removeConfirmationMessage = stringResource(R.string.cart_remove_item_message),
                        removeConfirmLabel = stringResource(R.string.cart_remove_confirm),
                        removeCancelLabel = stringResource(R.string.cart_remove_cancel),
                        increaseQuantityContentDescription = stringResource(R.string.cart_increase_quantity_cd),
                        decreaseQuantityContentDescription = stringResource(R.string.cart_decrease_quantity_cd),
                        removeItemContentDescription = stringResource(R.string.cart_remove_item_cd),
                        onIncreaseQuantity = { onIncreaseQuantity(line.id) },
                        onDecreaseQuantity = { onDecreaseQuantity(line.id) },
                        onRemoveItem = { onRemoveItem(line.id) }
                    )
                }
            }

            CartSummaryCard(
                subTotalAmount = cart.displaySubtotal,
                totalAmount = cart.displayTotal,
                totalTaxAmount = null,
                checkoutChargeAmount = cart.displayTotal,
                subtotalLabel = stringResource(R.string.cart_subtotal),
                taxLabel = stringResource(R.string.cart_tax),
                checkoutChargeLabel = stringResource(R.string.cart_checkout_charge),
                totalLabel = stringResource(R.string.cart_total)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = stringResource(R.string.proceed_to_checkout),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
