package com.example.qafilah.features.orders.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: Int,
    val processedAt: String,
    val financialStatus: String,
    val fulfillmentStatus: String,
    val totalAmount: String,
    val currencyCode: String,
    val syncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_line_items")
data class OrderLineItemEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val orderId: String,
    val title: String,
    val quantity: Int,
    val priceAmount: String,
    val currencyCode: String,
    val variantTitle: String?,
    val imageUrl: String?
)
