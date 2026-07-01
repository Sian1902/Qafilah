package com.example.qafilah.features.auth.data.mapper

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.graphql.storefront.GetCustomerQuery

fun GetCustomerQuery.Customer.toDomain(firebaseUid: String): AppUser {
    return AppUser(
        id = firebaseUid,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        phone = this.phone
    )
}