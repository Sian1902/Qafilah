package com.example.qafilah.features.assistant.data.remote

import com.google.gson.annotations.SerializedName

data class ChatResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("choices") val choices: List<ChoiceDto>
)

data class ChoiceDto(
    @SerializedName("message") val message: MessageDto
)