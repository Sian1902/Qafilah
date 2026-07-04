package com.example.qafilah.core.network

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


sealed class NetworkError(message: String) : Exception(message) {
    class GraphQLError(graphqlMessage: String) : NetworkError(graphqlMessage)
    class DataIsNull : NetworkError("Data is null")
    class Unknown(message: String = "Unknown network error") : NetworkError(message)
}

suspend fun <D : Operation.Data> safeApiCall(
    apiCall: suspend () -> ApolloResponse<D>
): D {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()

            if (response.hasErrors()) {
                val errorMessage =
                    response.errors?.firstOrNull()?.message ?: "Unknown GraphQL Error"
                throw NetworkError.GraphQLError(errorMessage)
            }

            response.data ?: throw NetworkError.DataIsNull()

        } catch (e: Exception) {
            if (e is NetworkError) throw e
            throw NetworkError.Unknown(e.message ?: "Unknown network error")
        }
    }
}