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
    subtotal: String,
    discountAmount: String? = null, // NEW: Optional discount parameter
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
            SummaryRow(label = "Subtotal", amount = subtotal)

            // NEW: Conditionally render the discount row
            if (!discountAmount.isNullOrBlank() && discountAmount != "$0.00") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Discount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.primary // Highlights the savings
                    )
                    Text(
                        text = "-$discountAmount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(label = "Shipping", amount = shippingAmount)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(label = "Taxes", amount = tax)

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = colorScheme.onSurface.copy(alpha = 0.12f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground
                )
                Text(
                    text = total,
                    style = MaterialTheme.typography.displayMedium,
                    color = colorScheme.primary
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