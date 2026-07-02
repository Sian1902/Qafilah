package com.example.qafilah.features.cart.domain.usecase

import com.example.qafilah.features.cart.domain.model.CartItemCommand
import com.example.qafilah.features.cart.domain.model.CartLine
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.cart.domain.repo.CartRepository

class ManageCartItemUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(command: CartItemCommand): StoreCart? {
        return when (command) {
            is CartItemCommand.OptimisticUpdate -> handleOptimisticUpdate(command)
            is CartItemCommand.OptimisticRemove -> handleOptimisticRemove(command)
            is CartItemCommand.SyncRemoteQuantity -> handleSyncRemoteQuantity(command)
            is CartItemCommand.SyncRemoteRemoval -> handleSyncRemoteRemoval(command)
            is CartItemCommand.Rollback -> handleRollback(command)
        }
    }

    private fun handleOptimisticUpdate(command: CartItemCommand.OptimisticUpdate): StoreCart? {
        val currentCart = repository.cartState.value ?: return null
        val fallbackState = currentCart

        val updatedLines = currentCart.lines.map { line ->
            if (line.id == command.lineId) line.copy(quantity = command.newQuantity) else line
        }

        updateLocalState(currentCart, updatedLines)
        return fallbackState
    }

    private fun handleOptimisticRemove(command: CartItemCommand.OptimisticRemove): StoreCart? {
        val currentCart = repository.cartState.value ?: return null
        val fallbackState = currentCart

        val updatedLines = currentCart.lines.filter { it.id != command.lineId }

        updateLocalState(currentCart, updatedLines)
        return fallbackState
    }

    private fun updateLocalState(currentCart: StoreCart, updatedLines: List<CartLine>) {
        val optimisticCart = currentCart.copy(
            lines = updatedLines,
            totalQuantity = updatedLines.sumOf { it.quantity }
        )
        repository.updateLocalCartState(optimisticCart)
    }

    private suspend fun handleSyncRemoteQuantity(command: CartItemCommand.SyncRemoteQuantity): StoreCart? {
        repository.updateCartItemRemote(command.lineId, command.newQuantity)
        return null
    }

    private suspend fun handleSyncRemoteRemoval(command: CartItemCommand.SyncRemoteRemoval): StoreCart? {
        repository.removeCartItemRemote(command.lineId)
        return null
    }

    private fun handleRollback(command: CartItemCommand.Rollback): StoreCart? {
        repository.updateLocalCartState(command.fallbackState)
        return null
    }
}