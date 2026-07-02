package com.example.qafilah.features.profile.domain.model

import com.example.qafilah.features.auth.domain.model.AppUser

data class CustomerProfile(
    val user: AppUser,
    val defaultAddress: Address?,
    val addresses: List<Address>,
    val orders: List<Order>
)