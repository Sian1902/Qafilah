package com.example.qafilah.features.checkout.presentation.shared

import com.example.qafilah.features.checkout.domain.model.*

object CheckoutMocks {

    private val mockLines = listOf(
        CheckoutLineItem(
            id = "gid://shopify/CartLine/9f8970a2-47df-4c93",
            productTitle = "The Collection Snowboard: Liquid",
            variantTitle = "Default Title",
            quantity = 1,
            price = CheckoutMoney.from("749.95", "USD"),
            imageUrl = "https://cdn.shopify.com/s/files/1/0715/6128/1613/files/Main_b13ad453-477c-4ed1-9b43-81f3345adfd6.jpg"
        )
    )


    val initialCart = CheckoutCart(
        id = "gid://shopify/Cart/hWNDvu3ESBK11zePlKiPEfoY",
        cost = CheckoutCost(
            subtotalAmount = CheckoutMoney.from("749.95", "USD"),
            totalAmount = CheckoutMoney.from("674.96", "USD"),
            totalTaxAmount = null
        ),
        deliveryGroups = emptyList(),
        lines = mockLines
    )


    val cartWithAddress = initialCart.copy(
        cost = initialCart.cost.copy(
            totalTaxAmount = CheckoutMoney.from("45.00", "USD"),
            totalAmount = CheckoutMoney.from("719.96", "USD")
        ),
        deliveryGroups = listOf(
            DeliveryGroup(
                id = "gid://shopify/CartDeliveryGroup/1",
                deliveryOptions = listOf(
                    DeliveryOption(
                        handle = "92aa3f22b4608235fb7dfc360bdd8b50",
                        title = "Standard Shipping",
                        estimatedCost = CheckoutMoney.from("0.00", "USD")
                    ),
                    DeliveryOption(
                        handle = "b82c208d77cc615cb38b955c068c84cc",
                        title = "Express Shipping",
                        estimatedCost = CheckoutMoney.from("15.00", "USD")
                    )
                )
            )
        )
    )


    val cartWithExpressShipping = cartWithAddress.copy(
        cost = cartWithAddress.cost.copy(
            totalAmount = CheckoutMoney.from("734.96", "USD")
        )
    )
}