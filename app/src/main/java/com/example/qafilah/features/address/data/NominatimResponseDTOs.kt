package com.example.qafilah.features.address.data

import com.example.qafilah.features.address.domain.model.AddressSuggestion
import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("address") val addressDetails: NominatimAddressDetails?
)

data class NominatimAddressDetails(
    val road: String?,
    val city: String?,
    val town: String?,
    val state: String?,
    val country: String?,
    val postcode: String?
)

fun NominatimResponse.toDomainModel(): AddressSuggestion {
    return AddressSuggestion(
        displayName = this.displayName,
        street = this.addressDetails?.road ?: "",
        city = this.addressDetails?.city ?: this.addressDetails?.town ?: "",
        province = this.addressDetails?.state ?: "",
        country = this.addressDetails?.country ?: "",
        zipCode = this.addressDetails?.postcode ?: ""
    )
}