package com.example.qafilah.features.checkout.presentation.shared

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import com.example.qafilah.features.checkout.presentation.address.CheckoutAddressScreen
import com.example.qafilah.features.checkout.presentation.summary.CheckoutSummaryScreen
import com.example.qafilah.features.checkout.presentation.payment.CheckoutPaymentScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutScreen(
    sharedViewModel: CheckoutSharedViewModel = koinViewModel(),
    onNavigateToAddress: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> CheckoutAddressScreen(
                sharedViewModel = sharedViewModel,
                onNavigateToSummary = {
                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                },
                onNavigateToAddEditAddress = { onNavigateToAddress() }
            )
            1 -> CheckoutSummaryScreen(
                sharedViewModel = sharedViewModel,
                onNavigateToPayment = {
                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                }
            )
            2 -> CheckoutPaymentScreen(
                sharedViewModel = sharedViewModel,
                onCheckoutComplete = { /* Handle success */ }
            )
        }
    }
}