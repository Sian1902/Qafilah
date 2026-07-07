package com.example.qafilah.features.checkout.data.mapper

import com.apollographql.apollo.api.Optional
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.checkout.domain.model.CheckoutLineItem
import com.example.qafilah.graphql.admin.type.DraftOrderLineItemInput
import com.example.qafilah.graphql.admin.type.MailingAddressInput

fun ShippingAddress.toAdminMailingAddressInput(user: AppUser): MailingAddressInput {
    return MailingAddressInput(
        firstName = Optional.present(user.firstName ?: "Customer"),
        lastName = Optional.present(user.lastName ?: ""),
        address1 = Optional.present(this.street),
        address2 = Optional.presentIfNotNull(this.locationDetails.takeIf { it.isNotBlank() }),
        city = Optional.present("Cairo"),
        country = Optional.present("EG"),
        phone = Optional.presentIfNotNull(user.phone)
    )
}

fun CheckoutLineItem.toAdminLineItemInput(): DraftOrderLineItemInput {
    return DraftOrderLineItemInput(
        variantId = Optional.present(this.variantId),
        quantity = this.quantity
    )
}