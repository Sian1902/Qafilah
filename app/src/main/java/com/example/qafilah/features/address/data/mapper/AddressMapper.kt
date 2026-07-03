package com.example.qafilah.features.address.data.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import com.example.qafilah.features.address.domain.model.CustomerAddress
import com.example.qafilah.features.address.domain.model.ShippingAddress

fun CustomerAddress.toDomain(): ShippingAddress {
    return ShippingAddress(
        id = id,
        label = if (address1.isNullOrBlank()) "Address" else "Home",
        icon = Icons.Default.Home,
        street = listOfNotNull(address1, address2).filter { it.isNotBlank() }.joinToString(separator = ", "),
        locationDetails = listOfNotNull(city, province, country, zip).filter { it.isNullOrBlank().not() }.joinToString(separator = ", "),
        isDefault = false
    )
}

fun ShippingAddress.toData(): CustomerAddress {
    val parts = locationDetails.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    return CustomerAddress(
        id = id,
        firstName = null,
        lastName = null,
        address1 = street,
        address2 = null,
        city = parts.getOrNull(0),
        province = parts.getOrNull(1),
        country = parts.getOrNull(2),
        zip = parts.getOrNull(3),
        phone = null
    )
}
