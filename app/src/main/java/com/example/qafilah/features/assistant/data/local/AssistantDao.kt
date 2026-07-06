package com.example.qafilah.features.assistant.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssistantDao {
    @Insert
    suspend fun insertMessage(message: AssistantMessageEntity)

    @Query("SELECT * FROM chat_history WHERE userId = :userId ORDER BY timestamp ASC")
    fun getAllMessages(userId: String): Flow<List<AssistantMessageEntity>>

    @Query("SELECT * FROM chat_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(userId: String, limit: Int): List<AssistantMessageEntity>

    @Query("DELETE FROM chat_history WHERE userId = :userId")
    suspend fun clearHistory(userId: String)
}