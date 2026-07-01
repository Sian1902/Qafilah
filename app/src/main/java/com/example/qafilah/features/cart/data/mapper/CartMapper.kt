package com.example.qafilah.features.cart.data.mapper

import com.example.qafilah.features.cart.domain.model.CartCost
import com.example.qafilah.features.cart.domain.model.CartLine
import com.example.qafilah.features.cart.domain.model.CartLineCost
import com.example.qafilah.features.cart.domain.model.DiscountCode
import com.example.qafilah.features.cart.domain.model.Money
import com.example.qafilah.features.cart.domain.model.ProductImage
import com.example.qafilah.features.cart.domain.model.ProductSummary
import com.example.qafilah.features.cart.domain.model.ProductVariant
import com.example.qafilah.features.cart.domain.model.SelectedOption
import com.example.qafilah.features.cart.domain.model.StoreCart
import com.example.qafilah.graphql.storefront.GetCartQuery
import com.example.qafilah.graphql.storefront.fragment.MoneyFields

fun GetCartQuery.Cart.toDomain(): StoreCart = StoreCart(
    id = id,
    checkoutUrl = checkoutUrl.toString(),
    totalQuantity = totalQuantity,
    discountCodes = discountCodes.map { it.toDomain() },
    cost = cost.toDomain(),
    lines = lines.edges.map { it.node.toDomain() }
)

private fun GetCartQuery.DiscountCode.toDomain(): DiscountCode = DiscountCode(
    code = code,
    applicable = applicable
)

private fun GetCartQuery.Cost.toDomain(): CartCost = CartCost(
    subtotalAmount = subtotalAmount.moneyFields.toMoney(),
    totalAmount = totalAmount.moneyFields.toMoney(),
    totalTaxAmount = totalTaxAmount?.moneyFields?.toMoney(),
    checkoutChargeAmount = checkoutChargeAmount.moneyFields.toMoney()
)

private fun GetCartQuery.Node.toDomain(): CartLine {
    val variantFragment = merchandise.onProductVariant
        ?: error("Unsupported merchandise type for cart line $id")

    return CartLine(
        id = id,
        quantity = quantity,
        cost = cost.toDomain(),
        merchandise = variantFragment.toDomain()
    )
}

private fun GetCartQuery.Cost1.toDomain(): CartLineCost = CartLineCost(
    totalAmount = totalAmount.moneyFields.toMoney(),
    amountPerQuantity = amountPerQuantity.moneyFields.toMoney(),
    compareAtAmountPerQuantity = compareAtAmountPerQuantity?.moneyFields?.toMoney()
)

private fun GetCartQuery.OnProductVariant.toDomain(): ProductVariant = ProductVariant(
    id = id,
    title = title,
    price = price.moneyFields.toMoney(),
    compareAtPrice = compareAtPrice?.moneyFields?.toMoney(),
    availableForSale = availableForSale,
    quantityAvailable = quantityAvailable,
    selectedOptions = selectedOptions.map { SelectedOption(name = it.name, value = it.value) },
    image = image?.let { ProductImage(url = it.url.toString(), altText = it.altText) },
    product = ProductSummary(
        id = product.id,
        title = product.title,
        vendor = product.vendor
    )
)

private fun MoneyFields.toMoney(): Money = Money.from(amount as String, currencyCode.rawValue)