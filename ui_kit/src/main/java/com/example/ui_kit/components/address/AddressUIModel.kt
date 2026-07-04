package com.example.ui_kit.components.address

import androidx.compose.ui.graphics.vector.ImageVector

data class AddressUiModel(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val street: String,
    val locationDetails: String,
    val isDefault: Boolean
)