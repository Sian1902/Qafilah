package com.example.qafilah.features.orders.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.ui_kit.components.orders.OrderCard
import com.example.ui_kit.components.orders.OrdersEmptyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel,
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.order_history_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading && state.orders.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.orders.isEmpty()) {
                OrdersEmptyView(
                    title = stringResource(R.string.orders_empty_title),
                    subtitle = stringResource(R.string.orders_empty_subtitle),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.orders) { order ->
                        val statusLabel = when (order.fulfillmentStatus) {
                            "FULFILLED" -> stringResource(R.string.order_status_fulfilled)
                            "UNFULFILLED" -> stringResource(R.string.order_status_processing)
                            else -> order.fulfillmentStatus
                        }

                        OrderCard(
                            orderNumber = stringResource(R.string.order_number_label, order.orderNumber),
                            date = stringResource(R.string.order_date_label, order.processedAt.substringBefore("T")),
                            totalPrice = "${order.totalPrice.amount} ${order.totalPrice.currencyCode}",
                            status = order.fulfillmentStatus,
                            statusLabel = statusLabel,
                            viewDetailsLabel = stringResource(R.string.order_view_details),
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }
            }
        }
    }
}
