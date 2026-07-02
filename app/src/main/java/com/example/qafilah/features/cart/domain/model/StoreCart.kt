package com.example.qafilah.features.cart.domain.model

import java.math.BigDecimal

data class Money(
    val amount: BigDecimal,
    val currencyCode: String
) {
    companion object {
        fun from(amount: String, currencyCode: String) =
            Money(BigDecimal(amount), currencyCode)
    }
}

data class DiscountCode(
    val code: String,
    val applicable: Boolean
)

data class CartCost(
    val subtotalAmount: Money,
    val totalAmount: Money,
    val totalTaxAmount: Money?,
    val checkoutChargeAmount: Money
)

data class SelectedOption(
    val name: String,
    val value: String
)

data class ProductImage(
    val url: String,
    val altText: String?
)

data class ProductSummary(
    val id: String,
    val title: String,
    val vendor: String
)

data class ProductVariant(
    val id: String,
    val title: String,
    val price: Money,
    val compareAtPrice: Money?,
    val availableForSale: Boolean,
    val quantityAvailable: Int?,
    val selectedOptions: List<SelectedOption>,
    val image: ProductImage?,
    val product: ProductSummary
) {
    val isOnSale: Boolean
        get() = compareAtPrice != null && compareAtPrice.amount > price.amount
}

data class CartLineCost(
    val totalAmount: Money,
    val amountPerQuantity: Money,
    val compareAtAmountPerQuantity: Money?
)

data class CartLine(
    val id: String,
    val quantity: Int,
    val cost: CartLineCost,
    val merchandise: ProductVariant
)

data class StoreCart(
    val id: String,
    val checkoutUrl: String,
    val totalQuantity: Int,
    val discountCodes: List<DiscountCode>,
    val cost: CartCost,
    val lines: List<CartLine>
) {
    val isEmpty: Boolean get() = lines.isEmpty()
    val appliedDiscounts: List<DiscountCode> get() = discountCodes.filter { it.applicable }
}