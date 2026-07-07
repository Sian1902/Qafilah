package com.example.qafilah.features.profile.data.datasource

import com.example.qafilah.graphql.storefront.CustomerUpdateMutation
import com.example.qafilah.graphql.storefront.GetCustomerQuery
import com.example.qafilah.graphql.storefront.GetPersonalDetailsQuery
import com.example.qafilah.graphql.storefront.type.CustomerUpdateInput

interface ProfileRemoteDataSource {
    suspend fun getCustomerProfile(): GetCustomerQuery.Customer?
    suspend fun getPersonalDetails(): GetPersonalDetailsQuery.Customer?
    suspend fun updateCustomer(
        customerInput: CustomerUpdateInput
    ): CustomerUpdateMutation.CustomerUpdate?
}
