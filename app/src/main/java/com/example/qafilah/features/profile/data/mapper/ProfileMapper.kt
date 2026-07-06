package com.example.qafilah.features.profile.data.mapper

import com.example.qafilah.features.address.domain.model.CustomerAddress
import com.example.qafilah.features.auth.data.mapper.toDomain
import com.example.qafilah.features.profile.domain.model.CustomerProfile
import com.example.qafilah.features.orders.domain.model.Money
import com.example.qafilah.features.orders.domain.model.Order
import com.example.qafilah.features.orders.domain.model.OrderLineItem
import com.example.qafilah.graphql.storefront.GetCustomerQuery

fun GetCustomerQuery.Customer.toDomain(firebaseUid: String): CustomerProfile {
    val user = this.toDomain(firebaseUid)

    val defaultAddressData = this.defaultAddress
    val defaultAddress = defaultAddressData?.let {
        CustomerAddress(
            id = it.id,
            firstName = it.firstName ?: "",
            lastName = it.lastName ?: "",
            address1 = it.address1 ?: "",
            address2 = it.address2,
            city = it.city ?: "",
            province = it.province,
            country = it.country ?: "",
            zip = it.zip ?: "",
            phone = it.phone ?: ""
        )
    }

    val addresses = this.addresses.edges.mapNotNull { edge ->
        edge.node?.let {
            CustomerAddress(
                id = it.id,
                firstName = it.firstName ?: "",
                lastName = it.lastName ?: "",
                address1 = it.address1 ?: "",
                address2 = it.address2,
                city = it.city ?: "",
                province = it.province,
                country = it.country ?: "",
                zip = it.zip ?: "",
                phone = it.phone ?: ""
            )
        }
    }

    val orders = this.orders.edges.mapNotNull { edge ->
        edge.node?.let {
            Order(
                id = it.id,
                orderNumber = it.orderNumber,
                processedAt = it.processedAt.toString(),
                financialStatus = it.financialStatus?.toString() ?: "",
                fulfillmentStatus = it.fulfillmentStatus?.toString() ?: "",
                totalPrice = Money(
                    amount = it.currentTotalPrice.amount.toString(),
                    currencyCode = it.currentTotalPrice.currencyCode.toString()
                ),
                lineItems = it.lineItems.edges.mapNotNull { itemEdge ->
                    itemEdge.node?.let { item ->
                        OrderLineItem(
                            title = item.title,
                            quantity = item.quantity,
                            price = Money(
                                amount = item.originalTotalPrice.amount.toString(),
                                currencyCode = item.originalTotalPrice.currencyCode.toString()
                            ),
                            variantTitle = item.variant?.title,
                            imageUrl = item.variant?.image?.url as? String
                        )
                    }
                }
            )
        }
    }

    return CustomerProfile(
        user = user,
        defaultAddress = defaultAddress,
        addresses = addresses,
        orders = orders
    )
}
