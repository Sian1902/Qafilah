package com.example.qafilah.features.address.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui_kit.components.address.AddressUiModel

data class ShippingAddress(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val street: String,
    val locationDetails: String,
    val isDefault: Boolean = false
)

fun ShippingAddress.toUiModel(): AddressUiModel {
    return AddressUiModel(
        id = this.id,
        label = this.label,
        icon = this.icon,
        street = this.street,
        locationDetails = this.locationDetails,
        isDefault = this.isDefault
    )
}