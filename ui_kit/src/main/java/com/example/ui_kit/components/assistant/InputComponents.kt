package com.example.ui_kit.components.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ui_kit.R.drawable.ic_mic

@Composable
fun QafilahQuickActionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun QafilahChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholderText,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp, max = 140.dp), // Allows the input box to grow dynamically up to a limit
        shape = RoundedCornerShape(28.dp), // Maintains a clean pill shape even when wrapping text
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
            focusedContainerColor = Color.White.copy(alpha = 0.12f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        maxLines = 4, // Lets long text wrap up to 4 lines before scrolling internally
        trailingIcon = {
            // Moving the Dynamic Button Container here ensures text never runs under it
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
            ) {
                if (value.isNotBlank()) {
                    IconButton(
                        onClick = onSendClick,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painterResource(android.R.drawable.ic_menu_send),
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                } else {
                    IconButton(
                        onClick = onMicClick,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painterResource(com.example.ui_kit.R.drawable.ic_mic),
                            contentDescription = "Speak",
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    )
}