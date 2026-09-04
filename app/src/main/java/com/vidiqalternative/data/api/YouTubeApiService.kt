package com.vidiqalternative.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("order") order: String = "relevance",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): Response<YouTubeSearchResponse>

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "snippet,statistics,contentDetails",
        @Query("id") id: String,
        @Query("key") apiKey: String
    ): Response<VideoListResponse>

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") part: String = "snippet,statistics,contentDetails",
        @Query("id") id: String,
        @Query("key") apiKey: String
    ): Response<ChannelListResponse>

    @GET("search")
    suspend fun searchChannels(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "channel",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): Response<YouTubeSearchResponse>

    @GET("videos")
    suspend fun getPopularVideos(
        @Query("part") part: String = "snippet,statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") regionCode: String = "TR",
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String
    ): Response<VideoListResponse>
}
