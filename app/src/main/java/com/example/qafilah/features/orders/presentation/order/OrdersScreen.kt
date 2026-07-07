package com.example.qafilah.features.orders.presentation.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.R
import com.example.ui_kit.components.orders.OrderCard
import com.example.ui_kit.components.orders.OrderCardUiModel
import com.example.ui_kit.components.orders.OrdersEmptyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel,
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val unknownErrorMessage = stringResource(R.string.error_unknown)
    val labels = OrderLabels(
        orderNumberPrefix = stringResource(R.string.order_number_label, "").trim(),
        datePrefix = stringResource(R.string.order_date_label, "").trim(),
        fulfilledLabel = stringResource(R.string.order_status_fulfilled),
        processingLabel = stringResource(R.string.order_status_processing)
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadOrders(unknownErrorMessage, labels)
    }

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
            when {
                state.isLoading && state.orders.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = {
                            viewModel.loadOrders(
                                unknownErrorMessage = unknownErrorMessage,
                                labels = labels
                            )
                        }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                state.orders.isEmpty() -> {
                    OrdersEmptyView(
                        title = stringResource(R.string.orders_empty_title),
                        subtitle = stringResource(R.string.orders_empty_subtitle),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.orders) { order ->
                            OrderCard(
                                order = OrderCardUiModel(
                                    orderNumber = order.orderNumber,
                                    date = order.date,
                                    totalPrice = order.totalPrice,
                                    status = order.status,
                                    statusLabel = order.statusLabel
                                ),
                                viewDetailsLabel = stringResource(R.string.order_view_details),
                                onClick = { onOrderClick(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
