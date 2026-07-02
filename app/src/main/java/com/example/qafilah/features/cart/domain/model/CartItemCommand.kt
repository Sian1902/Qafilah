package com.example.qafilah.features.cart.domain.model

sealed class CartItemCommand {
    data class OptimisticUpdate(val lineId: String, val newQuantity: Int) : CartItemCommand()
    data class OptimisticRemove(val lineId: String) : CartItemCommand()

    data class SyncRemoteQuantity(val lineId: String, val newQuantity: Int) : CartItemCommand()
    data class SyncRemoteRemoval(val lineId: String) : CartItemCommand()

    data class Rollback(val fallbackState: StoreCart) : CartItemCommand()
}