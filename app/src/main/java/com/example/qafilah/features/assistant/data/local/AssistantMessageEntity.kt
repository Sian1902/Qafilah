package com.example.qafilah.features.assistant.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.qafilah.features.assistant.domain.model.ChatMessage

@Entity(tableName = "chat_history")
data class AssistantMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val role: String,
    val content: String,
    val timestamp: Long
)

fun AssistantMessageEntity.toDomain() = ChatMessage(
    id = id,
    userId = userId,
    role = role,
    content = content,
    timestamp = timestamp
)

fun ChatMessage.toEntity() = AssistantMessageEntity(
    userId = userId,
    role = role,
    content = content,
    timestamp = timestamp
)