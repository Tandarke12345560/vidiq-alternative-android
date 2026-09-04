package com.vidiqalternative.data.repository

import com.vidiqalternative.BuildConfig
import com.vidiqalternative.data.api.ChannelItem
import com.vidiqalternative.data.api.SearchItem
import com.vidiqalternative.data.api.VideoItem
import com.vidiqalternative.data.api.YouTubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeRepository @Inject constructor(
    private val youtubeService: YouTubeApiService
) {

    private val apiKey: String = BuildConfig.YOUTUBE_API_KEY

    suspend fun searchVideos(
        query: String,
        maxResults: Int = 10
    ): Result<List<SearchItem>> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("YouTube API anahtarı tanımlanmamış"))
            }

            val response = youtubeService.searchVideos(
                query = query,
                maxResults = maxResults,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val items = response.body()?.items ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception("Arama hatası: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVideoDetails(videoId: String): Result<VideoItem?> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("YouTube API anahtarı tanımlanmamış"))
            }

            val response = youtubeService.getVideoDetails(
                id = videoId,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val item = response.body()?.items?.firstOrNull()
                Result.success(item)
            } else {
                Result.failure(Exception("Video detayı alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChannelDetails(channelId: String): Result<ChannelItem?> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("YouTube API anahtarı tanımlanmamış"))
            }

            val response = youtubeService.getChannelDetails(
                id = channelId,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val item = response.body()?.items?.firstOrNull()
                Result.success(item)
            } else {
                Result.failure(Exception("Kanal detayı alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchChannels(
        query: String,
        maxResults: Int = 10
    ): Result<List<SearchItem>> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("YouTube API anahtarı tanımlanmamış"))
            }

            val response = youtubeService.searchChannels(
                query = query,
                maxResults = maxResults,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val items = response.body()?.items ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception("Kanal arama hatası: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularVideos(regionCode: String = "TR"): Result<List<VideoItem>> =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) {
                    return@withContext Result.failure(Exception("YouTube API anahtarı tanımlanmamış"))
                }

                val response = youtubeService.getPopularVideos(
                    regionCode = regionCode,
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    val items = response.body()?.items ?: emptyList()
                    Result.success(items)
                } else {
                    Result.failure(Exception("Popüler videolar alınamadı: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun analyzeVideoSeo(videoId: String): Result<VideoSeoAnalysis> {
        val videoResult = getVideoDetails(videoId)
        val video = videoResult.getOrElse { return Result.failure(it) }
            ?: return Result.failure(Exception("Video bulunamadı"))

        val viewCount = video.statistics.viewCount.toLongOrNull() ?: 0
        val likeCount = video.statistics.likeCount?.toLongOrNull() ?: 0
        val commentCount = video.statistics.commentCount?.toLongOrNull() ?: 0

        val engagementRate = if (viewCount > 0) {
            ((likeCount + commentCount).toDouble() / viewCount * 100)
        } else 0.0

        val titleLength = video.snippet.title.length
        val descLength = video.snippet.description.length
        val tagCount = 0

        var seoScore = 0

        if (titleLength in 40..70) seoScore += 25
        else if (titleLength in 30..100) seoScore += 15

        if (descLength > 200) seoScore += 25
        else if (descLength > 100) seoScore += 15

        if (engagementRate > 5) seoScore += 25
        else if (engagementRate > 2) seoScore += 15

        if (viewCount > 1000) seoScore += 25
        else if (viewCount > 100) seoScore += 15

        return Result.success(
            VideoSeoAnalysis(
                videoId = videoId,
                title = video.snippet.title,
                channelName = video.snippet.channelTitle,
                viewCount = viewCount,
                likeCount = likeCount,
                commentCount = commentCount,
                engagementRate = engagementRate,
                titleLength = titleLength,
                descriptionLength = descLength,
                seoScore = seoScore,
                thumbnailUrl = video.snippet.thumbnails.high.url
            )
        )
    }
}

data class VideoSeoAnalysis(
    val videoId: String,
    val title: String,
    val channelName: String,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long,
    val engagementRate: Double,
    val titleLength: Int,
    val descriptionLength: Int,
    val seoScore: Int,
    val thumbnailUrl: String
)
