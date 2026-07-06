package com.example.qafilah.features.profile.domain.model

import com.example.qafilah.features.auth.domain.model.AppUser
import com.example.qafilah.features.orders.domain.model.Order

data class CustomerProfile(
    val user: AppUser,
    val defaultAddress: Address?,
    val addresses: List<Address>,
    val orders: List<Order>
)