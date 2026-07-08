package com.example.qafilah.features.camelrun

enum class GameStatus {
    IDLE, PLAYING, GAME_OVER, WON
}

data class Obstacle(
    val x: Float,
    val width: Float,
    val height: Float,
    val isTall: Boolean
)

object GameConfig {
    const val GRAVITY = 1.8f
    const val JUMP_VELOCITY = -35f

    const val BASE_SPEED = 18f
    const val MAX_SPEED = 40f
    const val SPEED_INCREMENT = 0.005f

    const val TARGET_SCORE = 1000

    const val CAMEL_WIDTH = 100f
    const val CAMEL_HEIGHT = 100f
    const val CAMEL_X = 150f

    const val MIN_SPACING = 700f
    const val MAX_SPACING = 1200f
}