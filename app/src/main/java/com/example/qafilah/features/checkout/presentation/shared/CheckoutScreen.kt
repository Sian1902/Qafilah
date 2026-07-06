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
import com.example.qafilah.features.checkout.presentation.payment.ui.CheckoutPaymentScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutScreen(
    sharedViewModel: CheckoutSharedViewModel = koinViewModel(),
    onNavigateToAddress: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = pagerState.currentPage > 0) {
        if (pagerState.currentPage == 3) {
            onNavigateToHome()
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
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
                onNavigateToSuccess = {
                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                }
            )
            3 -> CheckoutSuccessScreen(
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}