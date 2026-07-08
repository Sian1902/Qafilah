package com.example.ui_kit.components.bottomnav


import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    items: List<BottomNavBarItem>,
    currentRoute: String?,
    onItemClick: (BottomNavBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Keeps it safely above Android's system gesture bar!
            .padding(horizontal = 16.dp, vertical = 16.dp), // Slightly increased vertical padding
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                // 1. ADD SHADOW HERE to create the 3D floating effect
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = MaterialTheme.colorScheme.primary, // Optional: gives a slight colored glow
                    spotColor = MaterialTheme.colorScheme.primary
                )
                .clip(RoundedCornerShape(40.dp))
                // 2. Made alpha 1.0f. Shadows behind transparent backgrounds can look muddy.
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomNavBarItemView(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavBarItemView(
    item: BottomNavBarItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) activeColor else inactiveColor
        )

        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isSelected) activeColor else Color.Transparent)
        )
    }
}

data class BottomNavBarItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
)