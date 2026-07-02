package com.example.ui_kit.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui_kit.components.shared.QafilahConfirmationDialog

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

    var codeToRemove by remember { mutableStateOf<String?>(null) }

    QafilahConfirmationDialog(
        isVisible = codeToRemove != null,
        title = "Remove Promo Code",
        message = "Are you sure you want to remove the code '${codeToRemove}'? Your cart totals will be recalculated.",
        yesButtonText = "Remove",
        noButtonText = "Cancel",
        onYes = {
            codeToRemove?.let { onRemoveDiscount(it) }
            codeToRemove = null
        },
        onNo = {
            codeToRemove = null
        }
    )

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
                Text(
                    text = "+ Add Code",
                    color = colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (appliedCodes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                appliedCodes.forEach { code ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .clickable { codeToRemove = code }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = code.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.size(6.dp))

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove $code",
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}