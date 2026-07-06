package com.example.qafilah.features.checkout.data.repo

import com.example.qafilah.features.checkout.data.datasource.PaymentRemoteDataSource
import com.example.qafilah.features.checkout.domain.model.PaymentIntention
import com.example.qafilah.features.checkout.domain.repo.PaymentRepository

class PaymentRepositoryImpl(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val publicKey: String
) : PaymentRepository {

    override suspend fun createCardPaymentIntention(
        amountInCents: Int,
        orderReference: String
    ): PaymentIntention {
        val response = remoteDataSource.createIntention(
            amountInCents = amountInCents,
            orderReference = orderReference
        )

        return PaymentIntention(
            publicKey = publicKey,
            clientSecret = response.client_secret,
            orderReference = orderReference
        )
    }
}