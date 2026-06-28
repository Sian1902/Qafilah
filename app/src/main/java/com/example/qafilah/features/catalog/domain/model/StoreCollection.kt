package com.example.qafilah.features.catalog.domain.model

data class StoreCollection(
    val id: String,
    val title: String,
    val handle: String,
    val description: String,
    val imageUrl: String?
)