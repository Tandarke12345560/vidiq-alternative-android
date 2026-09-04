package com.vidiqalternative.data.repository

import com.vidiqalternative.data.api.AICoachTools
import com.vidiqalternative.data.api.ChatRequest
import com.vidiqalternative.data.api.Message
import com.vidiqalternative.data.api.OpenRouterApiService
import com.vidiqalternative.data.api.ToolCall
import com.vidiqalternative.util.SystemPrompt
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val openRouterService: OpenRouterApiService,
    private val aiTools: AICoachTools,
    private val modelRepository: ModelRepository,
    private val gson: Gson
) {

    suspend fun chat(
        userMessage: String,
        systemPrompt: String = SystemPrompt.YOUTUBE_COACH,
        conversationHistory: MutableList<Message> = mutableListOf()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = modelRepository.getApiKeySync()
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("OpenRouter API anahtarı tanımlanmamış"))
            }

            val selectedModel = modelRepository.getSelectedModelSync()

            if (conversationHistory.isEmpty()) {
                conversationHistory.add(Message("system", systemPrompt))
            }
            conversationHistory.add(Message("user", userMessage))

            var request = ChatRequest(
                model = selectedModel,
                messages = conversationHistory.toList(),
                tools = aiTools.getToolDefinitions(),
                toolChoice = "auto"
            )

            var response = openRouterService.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            var iterations = 0
            while (iterations < 3) {
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string() ?: "Bilinmeyen hata"
                    return@withContext Result.failure(Exception("API hatası: ${response.code()} - $errorBody"))
                }

                val chatResponse = response.body() ?: break
                val choice = chatResponse.choices.firstOrNull() ?: break

                val assistantMessage = choice.message
                if (assistantMessage == null) {
                    return@withContext Result.failure(Exception("Yanıt alınamadı"))
                }

                if (assistantMessage.toolCalls != null && assistantMessage.toolCalls.isNotEmpty()) {
                    conversationHistory.add(
                        Message(
                            role = "assistant",
                            content = null,
                            toolCalls = assistantMessage.toolCalls
                        )
                    )

                    for (toolCall in assistantMessage.toolCalls) {
                        val toolResult = aiTools.handleToolCall(toolCall)
                        conversationHistory.add(
                            Message(
                                role = "tool",
                                content = toolResult,
                                toolCallId = toolCall.id
                            )
                        )
                    }

                    request = ChatRequest(
                        model = selectedModel,
                        messages = conversationHistory.toList(),
                        tools = aiTools.getToolDefinitions()
                    )

                    response = openRouterService.chatCompletion(
                        authorization = "Bearer $apiKey",
                        request = request
                    )
                    iterations++
                } else {
                    val content = assistantMessage.content ?: "Yanıt içeriği boş"
                    conversationHistory.add(Message("assistant", content))
                    return@withContext Result.success(content)
                }
            }

            val finalResponse = response.body()
            val finalContent = finalResponse?.choices?.firstOrNull()?.message?.content
                ?: "Yanıt alınamadı"

            conversationHistory.add(Message("assistant", finalContent))
            Result.success(finalContent)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeVideo(
        title: String,
        description: String,
        tags: List<String>,
        viewCount: Long
    ): Result<String> {
        val prompt = """
            Bu YouTube videosunu analiz et ve SEO önerileri ver:

            Başlık: $title
            Açıklama: $description
            Etiketler: ${tags.joinToString(", ")}
            Görüntülenme: $viewCount

            Lütfen şu konularda öneriler sun:
            1. Başlık optimizasyonu
            2. Açıklama iyileştirmeleri
            3. Etiket stratejisi
            4. Genel SEO skoru (1-100)
        """.trimIndent()

        return chat(prompt)
    }

    suspend fun generateContentIdeas(channelNiche: String): Result<String> {
        val prompt = """
            $channelNiche için YouTube video fikirleri üret.

            Lütfen:
            1. 10 farklı video fikri öner
            2. Her fikir için başlık öner
            3. Hedef anahtar kelimeleri belirt
            4. Tahmini potansiyeli değerlendir
            5. Trendlere uygunluğunu açıkla
        """.trimIndent()

        return chat(prompt)
    }

    suspend fun analyzeCompetitor(channelName: String): Result<String> {
        val prompt = """
            "$channelName" YouTube kanalını analiz et.

            Lütfen şu konularda bilgi ver:
            1. Kanalın uzmanlık alanı
            2. İçerik stratejisi
            3. Büyüme trendi
            4. Güçlü ve zayıf yönleri
            5. Rakipleriniz için öneriler
        """.trimIndent()

        return chat(prompt)
    }
}
