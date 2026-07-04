package com.example.qafilah.features.checkout.data.mapper

import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.features.checkout.domain.model.*
import com.example.qafilah.graphql.storefront.fragment.CheckoutCartDetails
import com.example.qafilah.graphql.storefront.fragment.MoneyFields

fun CheckoutCartDetails.toDomain(): CheckoutCart = CheckoutCart(
    id = id,
    cost = cost.toDomain(),
    deliveryGroups = deliveryGroups?.edges?.map { it.node.toDomain() } ?: emptyList(),
    lines = lines.edges.map { it.node.toDomain() }
)

private fun CheckoutCartDetails.Node1.toDomain(): CheckoutLineItem {
    val variant = merchandise.onProductVariant
        ?: error("Unsupported merchandise type for checkout line $id")

    return CheckoutLineItem(
        id = id,
        productTitle = variant.product.title,
        variantTitle = variant.title,
        quantity = quantity,
        price = variant.price.moneyFields.toMoney(),
        imageUrl = variant.image?.url?.toString()
    )
}

private fun CheckoutCartDetails.Cost.toDomain(): CheckoutCost = CheckoutCost(
    subtotalAmount = subtotalAmount.moneyFields.toMoney(),
    totalAmount = totalAmount.moneyFields.toMoney(),
    totalTaxAmount = totalTaxAmount?.moneyFields?.toMoney()
)

private fun CheckoutCartDetails.Node.toDomain(): DeliveryGroup = DeliveryGroup(
    id = id,
    deliveryOptions = deliveryOptions.map { it.toDomain() }
)

private fun CheckoutCartDetails.DeliveryOption.toDomain(): DeliveryOption = DeliveryOption(
    handle = handle,
    title = title ?: "",
    estimatedCost = estimatedCost.moneyFields.toMoney()
)

private fun MoneyFields.toMoney(): CheckoutMoney = CheckoutMoney.from(
    amount = amount as String,
    currencyCode = currencyCode.rawValue
)

fun StoreCart.toCheckoutCart(): CheckoutCart {
    return CheckoutCart(
        id = this.id,
        cost = CheckoutCost(
            subtotalAmount = CheckoutMoney(this.cost.subtotalAmount.amount, this.cost.subtotalAmount.currencyCode),
            totalAmount = CheckoutMoney(this.cost.totalAmount.amount, this.cost.totalAmount.currencyCode),
            totalTaxAmount = this.cost.totalTaxAmount?.let {
                CheckoutMoney(it.amount, it.currencyCode)
            }
        ),
        deliveryGroups = emptyList(),
        lines = this.lines.map { line ->
            CheckoutLineItem(
                id = line.id,
                productTitle = line.merchandise.product.title,
                variantTitle = line.merchandise.title,
                quantity = line.quantity,
                price = CheckoutMoney(line.merchandise.price.amount, line.merchandise.price.currencyCode),
                imageUrl = line.merchandise.image?.url
            )
        }
    )
}