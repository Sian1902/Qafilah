package com.example.qafilah.features.checkout.presentation.address

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qafilah.features.checkout.presentation.shared.CheckoutSharedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutAddressScreen(
    sharedViewModel: CheckoutSharedViewModel,
    addressViewModel: CheckoutAddressViewModel = koinViewModel(),
    onNavigateToSummary: () -> Unit
) {
    val cart by sharedViewModel.cartState.collectAsState()
    val isLoading by addressViewModel.isLoading.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Step 1: Address", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                addressViewModel.submitAddress(cart.id) { updatedCart ->
                    sharedViewModel.updateCartState(updatedCart)
                    onNavigateToSummary()
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Continue to Summary")
        }
    }
}