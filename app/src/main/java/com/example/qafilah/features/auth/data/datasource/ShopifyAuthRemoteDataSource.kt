package com.example.qafilah.features.auth.data.datasource

import com.example.qafilah.graphql.storefront.GetCustomerQuery
import com.example.qafilah.graphql.storefront.LoginCustomerMutation
import com.example.qafilah.graphql.storefront.LogoutCustomerMutation
import com.example.qafilah.graphql.storefront.RegisterCustomerMutation
import com.example.qafilah.graphql.storefront.type.CustomerAccessTokenCreateInput
import com.example.qafilah.graphql.storefront.type.CustomerCreateInput

interface ShopifyAuthRemoteDataSource {

    suspend fun registerCustomer(input: CustomerCreateInput): RegisterCustomerMutation.CustomerCreate?

    suspend fun loginCustomer(input: CustomerAccessTokenCreateInput): LoginCustomerMutation.CustomerAccessTokenCreate?

    suspend fun logoutCustomer(customerAccessToken: String): LogoutCustomerMutation.CustomerAccessTokenDelete?

    suspend fun getCustomerProfile(customerAccessToken: String): GetCustomerQuery.Customer?
}