package com.example.qafilah.features.camelrun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qafilah.R
import com.example.qafilah.features.catalog.domain.usecases.SaveAdCouponUseCase
import org.koin.compose.koinInject

@Composable
fun CamelRunScreen(
    onNavigateHome: () -> Unit
) {
    val gameEngine = remember { GameEngine() }

    val camelPainter = painterResource(id = R.drawable.ic_camel)
    val cactusPainter = painterResource(id = R.drawable.ic_cactus)
    val rockPainter = painterResource(id = R.drawable.ic_rock)

    val backgroundColor = MaterialTheme.colorScheme.background
    val groundColor = MaterialTheme.colorScheme.primary
    val saveCopounUseCase = koinInject<SaveAdCouponUseCase>()


    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { gameEngine.update() }
        }
    }

    LaunchedEffect(gameEngine.status) {
        if (gameEngine.status == GameStatus.WON) {
                saveCopounUseCase("CO-50")

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                gameEngine.jump()
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (gameEngine.camelY == 0f && gameEngine.status == GameStatus.IDLE) {
                gameEngine.initialize(size.width, size.height)
            }

            val groundY = size.height * 0.75f

            drawLine(
                color = groundColor,
                start = Offset(0f, groundY),
                end = Offset(size.width, groundY),
                strokeWidth = 12f
            )

            translate(left = GameConfig.CAMEL_X, top = gameEngine.camelY) {
                with(camelPainter) {
                    draw(size = Size(GameConfig.CAMEL_WIDTH, GameConfig.CAMEL_HEIGHT))
                }
            }

            gameEngine.obstacles.forEach { obstacle ->
                val obstaclePainter = if (obstacle.isTall) cactusPainter else rockPainter
                translate(left = obstacle.x, top = groundY - obstacle.height) {
                    with(obstaclePainter) {
                        draw(size = Size(obstacle.width, obstacle.height))
                    }
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.camel_run_score_label, gameEngine.score, GameConfig.TARGET_SCORE),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        when (gameEngine.status) {
            GameStatus.IDLE -> GameOverlay(
                title = stringResource(id = R.string.camel_run_ready_title),
                subtitle = stringResource(id = R.string.camel_run_ready_subtitle),
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                textColor = MaterialTheme.colorScheme.onPrimary
            )
            GameStatus.GAME_OVER -> GameOverlay(
                title = stringResource(id = R.string.camel_run_game_over_title),
                subtitle = stringResource(id = R.string.camel_run_game_over_subtitle),
                backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                textColor = MaterialTheme.colorScheme.onError
            )
            GameStatus.WON -> GameOverlay(
                title = stringResource(id = R.string.camel_run_won_title),
                subtitle = stringResource(id = R.string.camel_run_won_subtitle),
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                textColor = MaterialTheme.colorScheme.onPrimary,
                buttonText = stringResource(id = R.string.camel_run_return_home),
                onButtonClick = onNavigateHome
            )
            GameStatus.PLAYING -> { }
        }
    }
}
