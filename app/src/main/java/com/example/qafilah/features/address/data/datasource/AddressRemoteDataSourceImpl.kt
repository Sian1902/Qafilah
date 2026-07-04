package com.example.qafilah.features.address.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.qafilah.core.network.safeApiCall
import com.example.qafilah.features.address.domain.model.CustomerAddress
import com.example.qafilah.graphql.storefront.AddCustomerAddressMutation
import com.example.qafilah.graphql.storefront.DeleteCustomerAddressMutation
import com.example.qafilah.graphql.storefront.CustomerDefaultAddressUpdateMutation
import com.example.qafilah.graphql.storefront.GetCustomerQuery
import com.example.qafilah.graphql.storefront.UpdateCustomerAddressMutation
import com.example.qafilah.graphql.storefront.type.MailingAddressInput

class AddressRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : AddressRemoteDataSource {

    override suspend fun getAddresses(accessToken: String): List<CustomerAddress> {
        val response = safeApiCall {
            apolloClient.query(GetCustomerQuery(customerAccessToken = accessToken)).execute()
        }

        return response.customer?.addresses?.edges?.mapNotNull { edge ->
            edge.node?.let { node ->
                CustomerAddress(
                    id = node.id,
                    firstName = node.firstName,
                    lastName = node.lastName,
                    address1 = node.address1,
                    address2 = node.address2,
                    city = node.city,
                    province = node.province,
                    country = node.country,
                    zip = node.zip,
                    phone = node.phone,
                    isDefault = response.customer?.defaultAddress?.id == node.id
                )
            }
        }.orEmpty()
    }

    override suspend fun createAddress(accessToken: String, address: CustomerAddress): CustomerAddress {
        val input = MailingAddressInput(
            address1 = Optional.present(address.address1.orEmpty()),
            address2 = Optional.present(address.address2.orEmpty()),
            city = Optional.present(address.city.orEmpty()),
            province = Optional.present(address.province.orEmpty()),
            country = Optional.present(address.country.orEmpty()),
            zip = Optional.present(address.zip.orEmpty()),
            phone = Optional.present(address.phone.orEmpty()),
            firstName = Optional.present(address.firstName.orEmpty()),
            lastName = Optional.present(address.lastName.orEmpty())
        )

        val response = safeApiCall {
            apolloClient.mutation(
                AddCustomerAddressMutation(
                    customerAccessToken = accessToken,
                    address = input
                )
            ).execute()
        }

        val payload = response.customerAddressCreate ?: throw Exception("Create address failed")
        if (payload.customerUserErrors.isNotEmpty()) {
            throw Exception(payload.customerUserErrors.joinToString { it.message ?: "Unknown error" })
        }

        val createdAddress = payload.customerAddress ?: throw Exception("No address returned")
        return CustomerAddress(
            id = createdAddress.id,
            firstName = createdAddress.firstName,
            lastName = createdAddress.lastName,
            address1 = createdAddress.address1,
            address2 = createdAddress.address2,
            city = createdAddress.city,
            province = createdAddress.province,
            country = createdAddress.country,
            zip = createdAddress.zip,
            phone = createdAddress.phone
        )
    }

    override suspend fun updateAddress(accessToken: String, address: CustomerAddress): CustomerAddress {
        val input = MailingAddressInput(
            address1 = Optional.present(address.address1.orEmpty()),
            address2 = Optional.present(address.address2.orEmpty()),
            city = Optional.present(address.city.orEmpty()),
            province = Optional.present(address.province.orEmpty()),
            country = Optional.present(address.country.orEmpty()),
            zip = Optional.present(address.zip.orEmpty()),
            phone = Optional.present(address.phone.orEmpty()),
            firstName = Optional.present(address.firstName.orEmpty()),
            lastName = Optional.present(address.lastName.orEmpty())
        )

        val response = safeApiCall {
            apolloClient.mutation(
                UpdateCustomerAddressMutation(
                    customerAccessToken = accessToken,
                    id = address.id,
                    address = input
                )
            ).execute()
        }

        val payload = response.customerAddressUpdate ?: throw Exception("Update address failed")
        if (payload.customerUserErrors.isNotEmpty()) {
            throw Exception(payload.customerUserErrors.joinToString { it.message ?: "Unknown error" })
        }

        val updatedAddress = payload.customerAddress ?: throw Exception("No address returned")
        return CustomerAddress(
            id = updatedAddress.id,
            firstName = updatedAddress.firstName,
            lastName = updatedAddress.lastName,
            address1 = updatedAddress.address1,
            address2 = updatedAddress.address2,
            city = updatedAddress.city,
            province = updatedAddress.province,
            country = updatedAddress.country,
            zip = updatedAddress.zip,
            phone = updatedAddress.phone
        )
    }

    override suspend fun deleteAddress(accessToken: String, addressId: String) {
        val response = safeApiCall {
            apolloClient.mutation(
                DeleteCustomerAddressMutation(
                    customerAccessToken = accessToken,
                    id = addressId
                )
            ).execute()
        }

        val payload = response.customerAddressDelete ?: throw Exception("Delete address failed")
        if (payload.customerUserErrors.isNotEmpty()) {
            throw Exception(payload.customerUserErrors.joinToString { it.message ?: "Unknown error" })
        }
    }

    override suspend fun setDefaultAddress(accessToken: String, addressId: String) {
        val response = safeApiCall {
            apolloClient.mutation(
                CustomerDefaultAddressUpdateMutation(
                    customerAccessToken = accessToken,
                    addressId = addressId
                )
            ).execute()
        }

        val payload = response.customerDefaultAddressUpdate ?: throw Exception("Set default address failed")
        if (payload.customerUserErrors.isNotEmpty()) {
            throw Exception(payload.customerUserErrors.joinToString { it.message ?: "Unknown error" })
        }
    }
}
