package com.vidiqalternative.data.api

import com.google.gson.annotations.SerializedName

// OpenRouter Chat Models
data class ChatRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("temperature") val temperature: Double = 0.7,
    @SerializedName("max_tokens") val maxTokens: Int = 1000,
    @SerializedName("stream") val stream: Boolean = false,
    @SerializedName("tools") val tools: List<Tool>? = null,
    @SerializedName("tool_choice") val toolChoice: String? = null
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String?,
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>? = null,
    @SerializedName("tool_call_id") val toolCallId: String? = null
)

data class ChatResponse(
    @SerializedName("id") val id: String,
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("usage") val usage: Usage?
)

data class Choice(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: ResponseMessage?,
    @SerializedName("finish_reason") val finishReason: String?
)

data class ResponseMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String?,
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>? = null
)

data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

// Tool Calling Models
data class Tool(
    @SerializedName("type") val type: String = "function",
    @SerializedName("function") val function: FunctionDef
)

data class FunctionDef(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("parameters") val parameters: Any
)

data class ToolCall(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String = "function",
    @SerializedName("function") val function: FunctionCall
)

data class FunctionCall(
    @SerializedName("name") val name: String,
    @SerializedName("arguments") val arguments: String
)

// OpenRouter Models List Response
data class ModelsListResponse(
    @SerializedName("data") val data: List<ModelDto>,
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("links") val links: PaginationLinks?
)

data class ModelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("pricing") val pricing: ModelPricing?,
    @SerializedName("context_length") val contextLength: Int?,
    @SerializedName("supported_parameters") val supportedParameters: List<String>?,
    @SerializedName("architecture") val architecture: ModelArchitecture?,
    @SerializedName("top_provider") val topProvider: TopProvider?
)

data class ModelPricing(
    @SerializedName("prompt") val prompt: String?,
    @SerializedName("completion") val completion: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("discount") val discount: Double?
)

data class ModelArchitecture(
    @SerializedName("modality") val modality: String?,
    @SerializedName("input_modalities") val inputModalities: List<String>?,
    @SerializedName("output_modalities") val outputModalities: List<String>?
)

data class TopProvider(
    @SerializedName("max_completion_tokens") val maxCompletionTokens: Int?,
    @SerializedName("is_moderated") val isModerated: Boolean?
)

data class PaginationLinks(
    @SerializedName("next") val next: String?
)
