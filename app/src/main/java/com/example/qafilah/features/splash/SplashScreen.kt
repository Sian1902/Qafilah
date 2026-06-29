package com.example.qafilah.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.ui_kit.components.splash.BrandTitle
import com.example.ui_kit.components.splash.SplashLogo
import com.example.ui_kit.components.splash.SplashTagline
import com.example.ui_kit.theme.QafilahTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
        delay(2000)
        onSplashFinished()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .alpha(alpha.value)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        SplashLogo(
            logo = painterResource(id = R.drawable.ic_logo)
        )

        Spacer(modifier = Modifier.height(32.dp))

        BrandTitle()

        Spacer(modifier = Modifier.weight(1f))

        SplashTagline()

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    QafilahTheme {
        SplashScreen(
            onSplashFinished = {}
        )
    }
}