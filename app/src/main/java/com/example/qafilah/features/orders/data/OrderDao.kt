package com.example.qafilah.features.orders.data

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Transaction
    @Query("SELECT * FROM orders ORDER BY processedAt DESC")
    fun getOrdersWithLineItems(): Flow<List<OrderWithLineItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderWithLineItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(items: List<OrderLineItemEntity>)

    @Query("DELETE FROM orders WHERE id NOT IN (SELECT id FROM orders ORDER BY processedAt DESC LIMIT 10)")
    suspend fun pruneOldOrders()

    @Query("DELETE FROM order_line_items WHERE orderId NOT IN (SELECT id FROM orders)")
    suspend fun deleteOrphanLineItems()

    @Transaction
    suspend fun syncOrders(orders: List<OrderEntity>, lineItems: List<OrderLineItemEntity>) {
        insertOrders(orders)
        insertLineItems(lineItems)
        pruneOldOrders()
        deleteOrphanLineItems()
    }

    @Query("DELETE FROM orders")
    suspend fun clearOrders()

    @Query("DELETE FROM order_line_items")
    suspend fun clearLineItems()
}

data class OrderWithLineItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val lineItems: List<OrderLineItemEntity>
)
