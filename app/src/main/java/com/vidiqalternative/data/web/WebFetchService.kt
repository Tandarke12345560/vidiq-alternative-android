package com.vidiqalternative.data.web

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

sealed class WebContent {
    data class Success(
        val url: String,
        val title: String,
        val content: String,
        val description: String,
        val keywords: String,
        val contentLength: Int
    ) : WebContent()

    data class Error(val message: String) : WebContent()
}

@Singleton
class WebFetchService @Inject constructor(
    private val httpClient: OkHttpClient
) {

    companion object {
        private const val MAX_CONTENT_LENGTH = 50000
        private const val DEFAULT_EXTRACT_LENGTH = 10000
    }

    suspend fun fetchContent(
        url: String,
        extractText: Boolean = true,
        maxLength: Int = DEFAULT_EXTRACT_LENGTH
    ): WebContent = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", getRandomUserAgent())
            .header("Accept", "text/html,application/xhtml+xml")
            .header("Accept-Language", "tr-TR,tr;q=0.9,en;q=0.8")
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string()
                ?: return@withContext WebContent.Error("İçerik alınamadı")

            val doc = Jsoup.parse(html, url)

            doc.select("script, style, nav, footer, header, iframe, noscript").remove()

            val title = doc.title()
            val mainContent = if (extractText) {
                extractReadableText(doc, maxLength)
            } else {
                html.take(maxLength)
            }

            val description = doc.selectFirst("meta[name=description]")?.attr("content") ?: ""
            val keywords = doc.selectFirst("meta[name=keywords]")?.attr("content") ?: ""

            WebContent.Success(
                url = url,
                title = title,
                content = mainContent,
                description = description,
                keywords = keywords,
                contentLength = mainContent.length
            )
        } catch (e: java.net.SocketTimeoutException) {
            WebContent.Error("Bağlantı zaman aşımı")
        } catch (e: java.net.UnknownHostException) {
            WebContent.Error("Host bulunamadı: $url")
        } catch (e: Exception) {
            WebContent.Error("Hata: ${e.message}")
        }
    }

    private fun extractReadableText(doc: org.jsoup.nodes.Document, maxLength: Int): String {
        val mainContent = doc.selectFirst("article, main, .content, .post-content, .entry-content, .article-body")
            ?: doc.selectFirst("body")
            ?: doc

        val paragraphs = mainContent.select("p, h1, h2, h3, h4, li, td, th, pre, blockquote")
            .map { it.text().trim() }
            .filter { it.isNotBlank() }

        return paragraphs.joinToString("\n\n").take(maxLength)
    }

    private fun getRandomUserAgent(): String {
        val userAgents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
        )
        return userAgents.random()
    }
}
