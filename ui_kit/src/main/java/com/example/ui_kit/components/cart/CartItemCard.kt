package com.example.ui_kit.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui_kit.components.shared.QafilahConfirmationDialog
import com.example.ui_kit.theme.QafilahTheme

@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    price: String,
    quantity: Int,
    quantityAvailable: Int?,
    removeConfirmationTitle: String,
    removeConfirmationMessage: String,
    removeConfirmLabel: String,
    removeCancelLabel: String,
    increaseQuantityContentDescription: String,
    decreaseQuantityContentDescription: String,
    removeItemContentDescription: String,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemoveItem: () -> Unit,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(28.dp)
    val quantityShape = RoundedCornerShape(28.dp)
    val showConfirmDialog = remember { mutableStateOf(false) }

    QafilahConfirmationDialog(
        message = removeConfirmationMessage,
        title = removeConfirmationTitle,
        onYes = {
            showConfirmDialog.value = false
            onRemoveItem()
        },
        onNo = {
            showConfirmDialog.value = false
        },
        isVisible = showConfirmDialog.value,
        yesButtonText = removeConfirmLabel,
        noButtonText = removeCancelLabel
    )

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colorScheme.surface)
            .border(1.dp, colorScheme.onSurface.copy(alpha = 0.12f), cardShape)
            .padding(16.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hasImage = imageUrl.isNotBlank()

        if (hasImage) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.firstOrNull()?.toString() ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorScheme.onSurface
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .clip(quantityShape)
                    .background(colorScheme.surfaceVariant.copy(alpha = 0.95f))
                    .border(1.dp, colorScheme.onSurface.copy(alpha = 0.14f), quantityShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecreaseQuantity,
                    enabled = quantity > 1
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = decreaseQuantityContentDescription,
                        tint = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.size(2.dp))

                IconButton(
                    onClick = onIncreaseQuantity,
                    enabled = quantityAvailable != null && quantity < quantityAvailable
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = increaseQuantityContentDescription,
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        IconButton(onClick = { showConfirmDialog.value = true }) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = removeItemContentDescription,
                tint = colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardPreview() {
    QafilahTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {

        }
    }
}
