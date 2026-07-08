package com.example.qafilah.features.camelrun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class GameEngine {
    var status by mutableStateOf(GameStatus.IDLE)
        private set

    var camelY by mutableStateOf(0f)
        private set

    var obstacles by mutableStateOf<List<Obstacle>>(emptyList())
        private set

    var score by mutableStateOf(0)
        private set

    private var camelVelocity = 0f
    private var groundY = 0f
    private var screenWidth = 0f

    private var currentSpeed = GameConfig.BASE_SPEED
    private var nextSpawnDistance = 0f

    fun initialize(width: Float, height: Float) {
        screenWidth = width
        groundY = height * 0.75f
        resetGame()
    }

    private fun resetGame() {
        camelY = groundY - GameConfig.CAMEL_HEIGHT
        camelVelocity = 0f
        obstacles = emptyList()
        score = 0
        currentSpeed = GameConfig.BASE_SPEED
        nextSpawnDistance = GameConfig.MIN_SPACING
    }

    fun startGame() {
        resetGame()
        status = GameStatus.PLAYING
    }

    fun jump() {
        if (status == GameStatus.IDLE || status == GameStatus.GAME_OVER) {
            startGame()
            return
        }

        if (camelY >= groundY - GameConfig.CAMEL_HEIGHT) {
            camelVelocity = GameConfig.JUMP_VELOCITY
        }
    }

    fun update() {
        if (status != GameStatus.PLAYING) return

        // 1. Apply Gravity
        camelVelocity += GameConfig.GRAVITY
        camelY += camelVelocity

        if (camelY >= groundY - GameConfig.CAMEL_HEIGHT) {
            camelY = groundY - GameConfig.CAMEL_HEIGHT
            camelVelocity = 0f
        }

        if (currentSpeed < GameConfig.MAX_SPEED) {
            currentSpeed += GameConfig.SPEED_INCREMENT
        }

        var updatedObstacles = obstacles.map {
            it.copy(x = it.x - currentSpeed)
        }.filter {
            it.x + it.width > 0
        }

        val lastObstacleX = updatedObstacles.lastOrNull()?.x ?: 0f
        val distanceSinceLastSpawn = screenWidth - lastObstacleX

        if (updatedObstacles.isEmpty() || distanceSinceLastSpawn > nextSpawnDistance) {
            updatedObstacles = updatedObstacles + generateRandomObstacle()
            nextSpawnDistance = Random.nextFloat() * (GameConfig.MAX_SPACING - GameConfig.MIN_SPACING) + GameConfig.MIN_SPACING
        }

        obstacles = updatedObstacles

        score += 1
        if (score >= GameConfig.TARGET_SCORE) {
            status = GameStatus.WON
        }

        checkCollisions()
    }

    private fun generateRandomObstacle(): Obstacle {
        val isTall = Random.nextBoolean()

        return if (isTall) {
            Obstacle(
                x = screenWidth,
                width = 60f,
                height = 120f,
                isTall = true
            )
        } else {
            Obstacle(
                x = screenWidth,
                width = 110f,
                height = 60f,
                isTall = false
            )
        }
    }

    private fun checkCollisions() {
        val camelRight = GameConfig.CAMEL_X + GameConfig.CAMEL_WIDTH
        val camelBottom = camelY + GameConfig.CAMEL_HEIGHT

        for (obstacle in obstacles) {
            val obstacleRight = obstacle.x + obstacle.width
            val obstacleTop = groundY - obstacle.height

            val isOverlappingX = GameConfig.CAMEL_X < obstacleRight && camelRight > obstacle.x
            val isOverlappingY = camelY < groundY && camelBottom > obstacleTop

            if (isOverlappingX && isOverlappingY) {
                status = GameStatus.GAME_OVER
                break
            }
        }
    }
}