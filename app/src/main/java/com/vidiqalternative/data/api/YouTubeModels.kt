package com.vidiqalternative.data.api

import com.google.gson.annotations.SerializedName

// YouTube Search Response
data class YouTubeSearchResponse(
    @SerializedName("kind") val kind: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("items") val items: List<SearchItem>,
    @SerializedName("pageInfo") val pageInfo: PageInfo
)

data class SearchItem(
    @SerializedName("kind") val kind: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("id") val id: SearchId,
    @SerializedName("snippet") val snippet: Snippet
)

data class SearchId(
    @SerializedName("kind") val kind: String,
    @SerializedName("videoId") val videoId: String?,
    @SerializedName("channelId") val channelId: String?
)

data class Snippet(
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("channelId") val channelId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnails") val thumbnails: Thumbnails,
    @SerializedName("channelTitle") val channelTitle: String,
    @SerializedName("liveBroadcastContent") val liveBroadcastContent: String
)

data class Thumbnails(
    @SerializedName("default") val default: Thumbnail,
    @SerializedName("medium") val medium: Thumbnail,
    @SerializedName("high") val high: Thumbnail
)

data class Thumbnail(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class PageInfo(
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("resultsPerPage") val resultsPerPage: Int
)

// YouTube Video Details Response
data class VideoListResponse(
    @SerializedName("kind") val kind: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("items") val items: List<VideoItem>
)

data class VideoItem(
    @SerializedName("kind") val kind: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("id") val id: String,
    @SerializedName("snippet") val snippet: Snippet,
    @SerializedName("statistics") val statistics: Statistics,
    @SerializedName("contentDetails") val contentDetails: ContentDetails?
)

data class Statistics(
    @SerializedName("viewCount") val viewCount: String,
    @SerializedName("likeCount") val likeCount: String?,
    @SerializedName("commentCount") val commentCount: String?,
    @SerializedName("favoriteCount") val favoriteCount: String
)

data class ContentDetails(
    @SerializedName("duration") val duration: String,
    @SerializedName("dimension") val dimension: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("caption") val caption: String
)

// YouTube Channel Response
data class ChannelListResponse(
    @SerializedName("kind") val kind: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("items") val items: List<ChannelItem>
)

data class ChannelItem(
    @SerializedName("kind") val kind: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("id") val id: String,
    @SerializedName("snippet") val channelSnippet: ChannelSnippet,
    @SerializedName("statistics") val statistics: ChannelStatistics,
    @SerializedName("contentDetails") val contentDetails: ChannelContentDetails?
)

data class ChannelSnippet(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnails") val thumbnails: Thumbnails,
    @SerializedName("publishedAt") val publishedAt: String
)

data class ChannelStatistics(
    @SerializedName("viewCount") val viewCount: String,
    @SerializedName("subscriberCount") val subscriberCount: String,
    @SerializedName("videoCount") val videoCount: String,
    @SerializedName("hiddenSubscriberCount") val hiddenSubscriberCount: Boolean
)

data class ChannelContentDetails(
    @SerializedName("relatedPlaylists") val relatedPlaylists: RelatedPlaylists
)

data class RelatedPlaylists(
    @SerializedName("uploads") val uploads: String
)
