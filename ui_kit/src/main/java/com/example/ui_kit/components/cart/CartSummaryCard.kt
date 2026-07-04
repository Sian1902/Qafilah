package com.example.ui_kit.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui_kit.theme.QafilahTheme


@Composable
fun CartSummaryCard(
    subTotalAmount: String,
    totalAmount: String,
    totalTaxAmount: String?,
    checkoutChargeAmount: String,
    subtotalLabel: String,
    taxLabel: String,
    checkoutChargeLabel: String,
    totalLabel: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(28.dp)
    val extraLabel = if (totalTaxAmount != null) taxLabel else checkoutChargeLabel
    val extraAmount = totalTaxAmount ?: checkoutChargeAmount

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colorScheme.surface)
            .border(1.dp, colorScheme.outline.copy(alpha = 0.35f), cardShape)
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        SummaryRow(
            label = subtotalLabel,
            value = subTotalAmount,
            labelColor = colorScheme.onSurfaceVariant,
            valueColor = colorScheme.onSurface,
            valueStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(18.dp))

        SummaryRow(
            label = extraLabel,
            value = extraAmount,
            labelColor = colorScheme.primary,
            valueColor = colorScheme.primary,
            valueStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(18.dp))

        HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.25f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(18.dp))

        SummaryRow(
            label = totalLabel,
            value = totalAmount,
            labelColor = colorScheme.onSurface,
            valueColor = colorScheme.primary,
            valueStyle = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    valueStyle: androidx.compose.ui.text.TextStyle
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            style = valueStyle,
            color = valueColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartSummaryCardPreview() {
    QafilahTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {


        }
    }
}
