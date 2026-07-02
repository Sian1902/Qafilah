package com.example.ui_kit.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscountCodesCard(
    modifier: Modifier = Modifier,
    appliedCodes: List<String>,
    onAddClick: () -> Unit,
    onRemoveDiscount: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colorScheme.surface)
            .border(1.dp, colorScheme.outline.copy(alpha = 0.35f), cardShape)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Promo Codes",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            TextButton(onClick = onAddClick) {
                Text(text = "+ Add Code")
            }
        }

        if (appliedCodes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                appliedCodes.forEach { code ->
                    InputChip(
                        selected = true,
                        onClick = { onRemoveDiscount(code) },
                        label = {
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove $code",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}