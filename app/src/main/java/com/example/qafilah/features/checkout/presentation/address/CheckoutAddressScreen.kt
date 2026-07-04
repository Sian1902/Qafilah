package com.example.qafilah.features.checkout.presentation.address

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.address.domain.model.toUiModel
import com.example.qafilah.features.checkout.domain.model.CheckoutCart
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import com.example.ui_kit.components.checkout.CheckoutAddressCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutAddressScreen(
    sharedViewModel: CheckoutSharedViewModel,
    addressViewModel: CheckoutAddressViewModel = koinViewModel(),
    onNavigateToSummary: () -> Unit,
    onNavigateToAddEditAddress: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()
    val uiState by addressViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                addressViewModel.loadAddresses()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    CheckoutAddressContent(
        uiState,
        onNavigateToAddEditAddress,
        {
            addressViewModel.submitAddress(cart.id) { updatedCart ->
                sharedViewModel.updateCartState(updatedCart)
                onNavigateToSummary()
            }
        },
        {
            addressViewModel.selectAddress(it.id)
        }
    )
}

@Composable
private fun CheckoutAddressContent(
    uiState: CheckoutAddressUIState,
    onNavigateToAddEditAddress: () -> Unit,
    onContinueToSummary: () -> Unit,
    onSelectAddress: (ShippingAddress) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Shipping Address",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.isLoadingAddresses) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center,) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.addresses) { domainAddress ->
                    CheckoutAddressCard(
                        address = domainAddress.toUiModel(),
                        isSelected = domainAddress.id == uiState.selectedAddressId,
                        onClick = { onSelectAddress(domainAddress) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onNavigateToAddEditAddress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Add or Edit Addresses", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Button(
            onClick = {
                onContinueToSummary()
            },
            enabled = !uiState.isSubmitting && uiState.selectedAddressId != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Continue to Summary", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}