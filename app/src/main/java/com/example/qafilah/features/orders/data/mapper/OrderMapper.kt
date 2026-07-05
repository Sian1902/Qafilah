package com.example.qafilah.features.orders.data.mapper

import com.example.qafilah.features.orders.data.OrderEntity
import com.example.qafilah.features.orders.data.OrderLineItemEntity
import com.example.qafilah.features.orders.data.OrderWithLineItems
import com.example.qafilah.features.orders.domain.model.Money
import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.model.OrderLineItem
import com.example.qafilah.graphql.storefront.GetCustomerQuery

fun GetCustomerQuery.Customer.toOrdersDomain(): List<Order> {
    return this.orders.edges.mapNotNull { edge ->
        edge.node?.let { node ->
            Order(
                id = node.id,
                orderNumber = node.orderNumber,
                processedAt = node.processedAt.toString(),
                financialStatus = node.financialStatus?.toString() ?: "",
                fulfillmentStatus = node.fulfillmentStatus?.toString() ?: "",
                totalPrice = Money(
                    amount = node.currentTotalPrice.amount.toString(),
                    currencyCode = node.currentTotalPrice.currencyCode.toString()
                ),
                lineItems = node.lineItems.edges.mapNotNull { itemEdge ->
                    itemEdge.node?.let { item ->
                        OrderLineItem(
                            title = item.title,
                            quantity = item.quantity,
                            price = Money(
                                amount = item.originalTotalPrice.amount.toString(),
                                currencyCode = item.originalTotalPrice.currencyCode.toString()
                            ),
                            variantTitle = item.variant?.title,
                            imageUrl = item.variant?.image?.url?.toString()
                        )
                    }
                }
            )
        }
    }
}

fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        id = id,
        orderNumber = orderNumber,
        processedAt = processedAt,
        financialStatus = financialStatus,
        fulfillmentStatus = fulfillmentStatus,
        totalAmount = totalPrice.amount,
        currencyCode = totalPrice.currencyCode
    )
}

fun OrderLineItem.toEntity(orderId: String): OrderLineItemEntity {
    return OrderLineItemEntity(
        orderId = orderId,
        title = title,
        quantity = quantity,
        priceAmount = price.amount,
        currencyCode = price.currencyCode,
        variantTitle = variantTitle,
        imageUrl = imageUrl
    )
}

fun OrderWithLineItems.toDomain(): Order {
    return Order(
        id = order.id,
        orderNumber = order.orderNumber,
        processedAt = order.processedAt,
        financialStatus = order.financialStatus,
        fulfillmentStatus = order.fulfillmentStatus,
        totalPrice = Money(order.totalAmount, order.currencyCode),
        lineItems = lineItems.map { it.toDomain() }
    )
}

fun OrderLineItemEntity.toDomain(): OrderLineItem {
    return OrderLineItem(
        title = title,
        quantity = quantity,
        price = Money(priceAmount, currencyCode),
        variantTitle = variantTitle,
        imageUrl = imageUrl
    )
}
