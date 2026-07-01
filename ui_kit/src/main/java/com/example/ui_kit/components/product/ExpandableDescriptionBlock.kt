package com.example.ui_kit.components.product

import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun ExpandableDescriptionBlock(
    descriptionHtml: String,
    modifier: Modifier = Modifier
) {
    if (descriptionHtml.isBlank()) return

    var isExpanded by remember { mutableStateOf(false) }
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { isExpanded = !isExpanded }
            .padding(20.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Product Description",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        )

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                TextView(context).apply {
                    textSize = 15f
                    setTextColor(textColor.toArgb())
                    maxLines = if (isExpanded) Int.MAX_VALUE else 4
                    ellipsize = android.text.TextUtils.TruncateAt.END
                    setLineSpacing(4f, 1.1f)
                }
            },
            update = { textView ->
                textView.text =
                    HtmlCompat.fromHtml(descriptionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
                textView.maxLines = if (isExpanded) Int.MAX_VALUE else 4
            }
        )

        Text(
            text = if (isExpanded) "Read Less" else "Read More",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
