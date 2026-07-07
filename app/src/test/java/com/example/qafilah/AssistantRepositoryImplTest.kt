package com.example.qafilah.features.assistant.data

import app.cash.turbine.test
import com.example.qafilah.features.assistant.data.local.AssistantDao
import com.example.qafilah.features.assistant.data.local.AssistantMessageEntity
import com.example.qafilah.features.assistant.data.remote.AssistantRemoteDataSource
import com.example.qafilah.features.assistant.data.remote.N8nWebhookResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AssistantRepositoryImplTest {

    // 1. Declare the mocks
    @MockK
    private lateinit var dao: AssistantDao

    @MockK
    private lateinit var remoteDataSource: AssistantRemoteDataSource

    // The system under test (SUT)
    private lateinit var repository: AssistantRepositoryImpl

    @Before
    fun setUp() {
        // Initialize MockK annotations
        MockKAnnotations.init(this)

        // Inject the mocks directly into the repository
        repository = AssistantRepositoryImpl(dao, remoteDataSource)
    }

    @Test
    fun `sendMessageAndGetReply returns success and saves messages when network call succeeds`() = runTest {
        // Given
        val userId = "user_123"
        val userText = "Hello AI"
        val aiReply = "Hello Human!"

        // Mock DAO to do nothing when insert is called (just run successfully)
        coEvery { dao.insertMessage(any()) } just runs

        // Mock the RemoteDataSource to return a successful DTO
        val mockSuccessResponse = mockk<N8nWebhookResponse> {
            every { status } returns "success"
            every { data.message } returns aiReply
        }
        coEvery { remoteDataSource.fetchAssistantResponse(userText) } returns mockSuccessResponse

        // When
        val result = repository.sendMessageAndGetReply(userId, userText)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(aiReply, result.getOrNull()?.content)
        assertEquals("assistant", result.getOrNull()?.role)

        // Verify DAO was called exactly twice (once for user msg, once for AI msg)
        coVerify(exactly = 2) { dao.insertMessage(any()) }
    }

    @Test
    fun `sendMessageAndGetReply returns failure when network status is not success`() = runTest {
        // Given
        val userId = "user_123"
        val userText = "Hello AI"

        coEvery { dao.insertMessage(any()) } just runs

        // Mock a failed API response
        val mockFailedResponse = mockk<N8nWebhookResponse> {
            every { status } returns "error"
            every { data.message } returns ""
        }
        coEvery { remoteDataSource.fetchAssistantResponse(userText) } returns mockFailedResponse

        // When
        val result = repository.sendMessageAndGetReply(userId, userText)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Failed to get a valid response from the assistant", result.exceptionOrNull()?.message)

        // Verify DAO was called ONLY ONCE (User message inserted, but AI message skipped due to throw)
        coVerify(exactly = 1) { dao.insertMessage(any()) }
    }

    @Test
    fun `getChatHistory maps database entities to domain models correctly`() = runTest {
        // Given
        val userId = "user_123"
        val expectedMessage = "Test message"

        // Create a real instance of your Room Entity to properly test the .toDomain() mapper
        val realEntity = AssistantMessageEntity(
            userId = userId,
            role = "user",
            content = expectedMessage,
            timestamp = 1680000000L // Using a dummy timestamp, adjust if your entity requires an ID
        )

        val mockEntityList = listOf(realEntity)

        // Mock the DAO to return a Flow containing our list
        every { dao.getAllMessages(userId) } returns flowOf(mockEntityList)

        // When & Then using Turbine to test the flow
        repository.getChatHistory(userId).test {
            val emittedList = awaitItem()

            // Assert the size and mapped values
            assertEquals(mockEntityList.size, emittedList.size)
            assertEquals(expectedMessage, emittedList[0].content)
            assertEquals("user", emittedList[0].role)

            // Cancel flow collection since it's an infinite hot stream in Room
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearHistory calls dao clearHistory`() = runTest {
        // Given
        val userId = "user_123"
        coEvery { dao.clearHistory(userId) } just runs

        // When
        repository.clearHistory(userId)

        // Then
        coVerify(exactly = 1) { dao.clearHistory(userId) }
    }
}