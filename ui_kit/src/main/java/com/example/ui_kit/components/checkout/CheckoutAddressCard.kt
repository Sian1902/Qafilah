package com.example.ui_kit.components.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui_kit.components.address.AddressUiModel

@Composable
fun CheckoutAddressCard(
    address: AddressUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val glassBackground = if (isSelected) colorScheme.primary.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.05f)
    val glassBorder = if (isSelected) colorScheme.primary else Color.White.copy(alpha = 0.12f)
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(glassBackground)
            .border(if (isSelected) 2.dp else 1.dp, glassBorder, cardShape)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colorScheme.primary,
                    unselectedColor = colorScheme.onSurfaceVariant
                )
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = address.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onBackground
                    )

                    if (address.isDefault) {
                        ContainerBadge(text = "DEFAULT", colorScheme = colorScheme)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = address.street,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = address.locationDetails,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ContainerBadge(text: String, colorScheme: ColorScheme) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}