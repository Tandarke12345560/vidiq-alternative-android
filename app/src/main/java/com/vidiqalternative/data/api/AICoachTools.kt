package com.vidiqalternative.data.api

import com.vidiqalternative.data.web.DDGSearchService
import com.vidiqalternative.data.web.WebContent
import com.vidiqalternative.data.web.WebFetchService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AICoachTools @Inject constructor(
    private val ddgSearch: DDGSearchService,
    private val webFetch: WebFetchService,
    private val youtubeService: YouTubeApiService,
    private val gson: Gson
) {

    fun getToolDefinitions(): List<Tool> {
        return listOf(
            Tool(
                type = "function",
                function = FunctionDef(
                    name = "search_web",
                    description = "Web'de arama yapar. Güncel bilgileri, trend konuları ve rakip analizlerini bulmak için kullanılır.",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "query" to mapOf(
                                "type" to "string",
                                "description" to "Arama sorgusu"
                            ),
                            "region" to mapOf(
                                "type" to "string",
                                "description" to "Bölge kodu (örn: tr-tr, en-us)",
                                "default" to "tr-tr"
                            )
                        ),
                        "required" to listOf("query")
                    )
                )
            ),
            Tool(
                type = "function",
                function = FunctionDef(
                    name = "fetch_page",
                    description = "Bir web sayfasının içeriğini çeker. URL'nin içeriğini okumak için kullanılır.",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "url" to mapOf(
                                "type" to "string",
                                "description" to "Çekilecek sayfanın URL'si"
                            )
                        ),
                        "required" to listOf("url")
                    )
                )
            ),
            Tool(
                type = "function",
                function = FunctionDef(
                    name = "search_youtube",
                    description = "YouTube'da video araştırır. Video bulmak ve analiz etmek için kullanılır.",
                    parameters = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "query" to mapOf(
                                "type" to "string",
                                "description" to "YouTube arama sorgusu"
                            )
                        ),
                        "required" to listOf("query")
                    )
                )
            )
        )
    }

    suspend fun handleToolCall(toolCall: ToolCall): String = withContext(Dispatchers.IO) {
        try {
            val args = parseArguments(toolCall.function.arguments)

            when (toolCall.function.name) {
                "search_web" -> handleSearchWeb(args)
                "fetch_page" -> handleFetchPage(args)
                "search_youtube" -> handleSearchYouTube(args)
                else -> "Bilinmeyen araç: ${toolCall.function.name}"
            }
        } catch (e: Exception) {
            "Hata: ${e.message}"
        }
    }

    private fun parseArguments(json: String): Map<String, Any> {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private suspend fun handleSearchWeb(args: Map<String, Any>): String {
        val query = args["query"] as? String ?: return "Sorgu eksik"
        val region = args["region"] as? String ?: "tr-tr"

        val results = ddgSearch.search(query, region, maxResults = 5)

        if (results.isEmpty()) {
            return "Sonuç bulunamadı: $query"
        }

        return buildString {
            appendLine("🔍 Arama Sonuçları: $query")
            appendLine("---")
            results.forEachIndexed { index, result ->
                appendLine("${index + 1}. ${result.title}")
                appendLine("   📎 ${result.url}")
                if (result.snippet.isNotBlank()) {
                    appendLine("   📝 ${result.snippet.take(200)}")
                }
                appendLine()
            }
        }
    }

    private suspend fun handleFetchPage(args: Map<String, Any>): String {
        val url = args["url"] as? String ?: return "URL eksik"

        val content = webFetch.fetchContent(url, maxLength = 5000)

        return when (content) {
            is WebContent.Success -> buildString {
                appendLine("📄 Sayfa İçeriği")
                appendLine("---")
                appendLine("Başlık: ${content.title}")
                appendLine("URL: ${content.url}")
                if (content.description.isNotBlank()) {
                    appendLine("Açıklama: ${content.description}")
                }
                appendLine()
                appendLine("İçerik:")
                appendLine(content.content)
            }
            is WebContent.Error -> "Hata: ${content.message}"
        }
    }

    private suspend fun handleSearchYouTube(args: Map<String, Any>): String {
        val query = args["query"] as? String ?: return "Sorgu eksik"

        return try {
            val apiKey = com.vidiqalternative.BuildConfig.YOUTUBE_API_KEY
            if (apiKey.isBlank()) {
                return "YouTube API anahtarı tanımlanmamış"
            }

            val response = youtubeService.searchVideos(
                query = query,
                maxResults = 5,
                apiKey = apiKey
            )

            if (!response.isSuccessful) {
                return "YouTube API hatası: ${response.code()}"
            }

            val items = response.body()?.items ?: emptyList()

            if (items.isEmpty()) {
                return "YouTube'da sonuç bulunamadı: $query"
            }

            buildString {
                appendLine("🎬 YouTube Arama: $query")
                appendLine("---")
                items.forEachIndexed { index, item ->
                    val videoId = item.id.videoId ?: return@forEachIndexed
                    appendLine("${index + 1}. ${item.snippet.title}")
                    appendLine("   👤 Kanal: ${item.snippet.channelTitle}")
                    appendLine("   📅 Tarih: ${item.snippet.publishedAt.take(10)}")
                    appendLine("   🔗 https://youtube.com/watch?v=$videoId")
                    appendLine()
                }
            }
        } catch (e: Exception) {
            "YouTube arama hatası: ${e.message}"
        }
    }
}
