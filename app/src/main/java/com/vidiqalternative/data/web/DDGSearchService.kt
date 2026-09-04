package com.vidiqalternative.data.web

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String,
    val displayedUrl: String
)

@Singleton
class DDGSearchService @Inject constructor(
    private val httpClient: OkHttpClient
) {

    companion object {
        private const val DDG_HTML_URL = "https://html.duckduckgo.com/html/"
        private const val REQUEST_DELAY_MS = 2500L
    }

    suspend fun search(
        query: String,
        region: String = "tr-tr",
        timeFilter: String? = null,
        maxResults: Int = 10
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        delay(REQUEST_DELAY_MS)

        val formData = FormBody.Builder()
            .add("q", query)
            .add("b", "")
            .add("kl", region)
            .apply {
                timeFilter?.let { add("df", it) }
            }
            .build()

        val request = Request.Builder()
            .url(DDG_HTML_URL)
            .header("User-Agent", getRandomUserAgent())
            .header("Accept-Language", "tr-TR,tr;q=0.9,en;q=0.8")
            .header("Accept", "text/html,application/xhtml+xml")
            .post(formData)
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string() ?: return@withContext emptyList()
            parseSearchResults(html).take(maxResults)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseSearchResults(html: String): List<SearchResult> {
        val doc = Jsoup.parse(html)
        val results = mutableListOf<SearchResult>()

        doc.select("div.result.results_links, div.web-result").forEach { element ->
            val titleElement = element.selectFirst("a.result__a, h2 a")
            val snippetElement = element.selectFirst("div.result__snippet, .result-snippet")
            val urlElement = element.selectFirst("a.result__url, .result__url")

            if (titleElement != null) {
                val href = titleElement.attr("href")
                val cleanUrl = extractUrlFromRedirect(href)

                results.add(
                    SearchResult(
                        title = titleElement.text().trim(),
                        url = cleanUrl,
                        snippet = snippetElement?.text()?.trim() ?: "",
                        displayedUrl = urlElement?.text()?.trim() ?: ""
                    )
                )
            }
        }

        return results
    }

    private fun extractUrlFromRedirect(href: String): String {
        return if (href.contains("uddg=")) {
            try {
                val url = href.substringAfter("uddg=").substringBefore("&")
                java.net.URLDecoder.decode(url, "UTF-8")
            } catch (e: Exception) {
                href
            }
        } else {
            href
        }
    }

    private fun getRandomUserAgent(): String {
        val userAgents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0"
        )
        return userAgents.random()
    }
}
