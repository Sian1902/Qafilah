package com.example.ui_kit.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddReviewCard(
    title: String,
    onTitleChange: (String) -> Unit,
    body: String,
    onBodyChange: (String) -> Unit,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    isSubmitting: Boolean,
    onSubmitClick: () -> Unit,
    cardTitle: String,
    nameLabel: String,
    titleLabel: String,
    bodyLabel: String,
    submitButtonText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = cardTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..5).forEach { starIndex ->
                val isFilled = starIndex <= rating
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rate $starIndex stars",
                    tint = if (isFilled) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f),
                    modifier = Modifier
                        .width(32.dp)
                        .clickable { onRatingChange(starIndex) }
                )
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(titleLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            label = { Text(bodyLabel) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onSubmitClick,
            enabled = !isSubmitting && title.isNotBlank() && body.isNotBlank() && rating > 0,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(2.dp)
                )
            } else {
                Text(
                    text = submitButtonText,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}