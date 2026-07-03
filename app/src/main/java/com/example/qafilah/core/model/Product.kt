package com.example.qafilah.core.model

data class Product(
    val id: String,
    val title: String,
    val vendor: String,
    val productType: String,
    val imageUrl: String?,
    val priceAmount: String,
    val currencyCode: String
)