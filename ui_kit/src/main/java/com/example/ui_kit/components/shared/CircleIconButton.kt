package com.example.ui_kit.components.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    showBadge: Boolean = false,
    badgeColor: Color = MaterialTheme.colorScheme.error
) {
    Box(modifier = modifier.size(size)) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(containerColor)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor
            )
        }

        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
        }
    }
}