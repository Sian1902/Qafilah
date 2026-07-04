package com.example.qafilah.features.checkout.data.mapper

import com.apollographql.apollo.api.Optional
import com.example.qafilah.features.address.domain.model.ShippingAddress
import com.example.qafilah.graphql.storefront.type.DeliveryAddressInput
import com.example.qafilah.graphql.storefront.type.MailingAddressInput // Make sure to import this!


fun ShippingAddress.toDeliveryAddressInput(
    phone: String = "+12025550134",
    firstName: String = "John",
    lastName: String = "Doe",
    countryCode: String = "US"
): DeliveryAddressInput {

    val mailingAddress = MailingAddressInput(
        address1 = Optional.present("1600 Pennsylvania Avenue NW"),
        city = Optional.present("Washington"),
        province = Optional.present("DC"),
        zip = Optional.present("20500"),
        phone = Optional.present(phone),
        firstName = Optional.present(firstName),
        lastName = Optional.present(lastName),
        country = Optional.present(countryCode),

        address2 = Optional.absent(),
        company = Optional.absent()
    )

    return DeliveryAddressInput(
        deliveryAddress = Optional.present(mailingAddress),
        oneTimeUse = Optional.absent(),
        deliveryAddressValidationStrategy = Optional.absent(),
        customerAddressId = Optional.absent()
    )
}