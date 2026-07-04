package com.example.ui_kit.components.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@Composable
fun SplashLogo(
    logo: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val glowColor = MaterialTheme.colorScheme.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(120.dp)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(
                                80f,
                                0f,
                                0f,
                                glowColor.copy(alpha = 0.6f).toArgb()
                            )
                        }
                    }
                    canvas.drawCircle(
                        center = center,
                        radius = size.minDimension / 2,
                        paint = paint
                    )
                }
            }
    ) {
        Image(
            painter = logo,
            contentDescription = contentDescription,
            modifier = Modifier.size(80.dp)
        )
    }
}