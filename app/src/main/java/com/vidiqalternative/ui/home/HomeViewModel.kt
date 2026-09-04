package com.vidiqalternative.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidiqalternative.data.repository.YouTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PopularVideo(
    val id: String,
    val title: String,
    val channelTitle: String,
    val viewCount: String,
    val thumbnailUrl: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val youtubeRepository: YouTubeRepository
) : ViewModel() {

    private val _popularVideos = MutableStateFlow<List<PopularVideo>>(emptyList())
    val popularVideos: StateFlow<List<PopularVideo>> = _popularVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPopularVideos()
    }

    private fun loadPopularVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            youtubeRepository.getPopularVideos()
                .onSuccess { videos ->
                    _popularVideos.value = videos.map { video ->
                        PopularVideo(
                            id = video.id,
                            title = video.snippet.title,
                            channelTitle = video.snippet.channelTitle,
                            viewCount = video.statistics.viewCount,
                            thumbnailUrl = video.snippet.thumbnails.medium.url
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
            _isLoading.value = false
        }
    }
}
