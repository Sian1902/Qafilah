package com.example.ui_kit.components.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.unit.dp

@Composable
fun CheckoutSummaryCard(
    modifier: Modifier = Modifier,
    subtotalLabel: String,
    subtotal: String,
    discountLabel: String,
    discountAmount: String? = null,
    shippingLabel: String,
    taxLabel: String,
    totalLabel: String,
    tax: String,
    shippingAmount: String,
    total: String,
    isCalculating: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(1.dp, colorScheme.onSurface.copy(alpha = 0.08f), cardShape)
            .padding(20.dp)
    ) {
        if (isCalculating) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        } else {
            SummaryRow(label = subtotalLabel, amount = subtotal)

            if (!discountAmount.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = discountLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.primary
                    )
                    Text(
                        text = discountAmount,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(label = shippingLabel, amount = shippingAmount)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(label = taxLabel, amount = tax)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = colorScheme.onSurface.copy(alpha = 0.12f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = totalLabel,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground
                )
                Text(
                    text = total,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}