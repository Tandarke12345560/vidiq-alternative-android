package com.vidiqalternative.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidiqalternative.data.repository.YouTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResultItem(
    val videoId: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val youtubeRepository: YouTubeRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResults: StateFlow<List<SearchResultItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            youtubeRepository.searchVideos(query)
                .onSuccess { items ->
                    _searchResults.value = items.map { item ->
                        SearchResultItem(
                            videoId = item.id.videoId ?: "",
                            title = item.snippet.title,
                            channelTitle = item.snippet.channelTitle,
                            thumbnailUrl = item.snippet.thumbnails.medium.url
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                    _searchResults.value = emptyList()
                }
            _isLoading.value = false
        }
    }
}
