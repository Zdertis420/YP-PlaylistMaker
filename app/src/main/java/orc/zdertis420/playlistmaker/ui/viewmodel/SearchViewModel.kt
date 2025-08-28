package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.interactor.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.SearchState
import orc.zdertis420.playlistmaker.utils.NetworkUtil


class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor,
    private val networkUtil: NetworkUtil
) : ViewModel() {

    companion object {
        private const val SEARCH_DELAY = 2000L
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var latestSearchJob: Job? = null
    private var lastSearchedQuery: String? = null
    private val tracksHistoryList: MutableList<Track> = trackHistoryInteractor.getTrackHistory()

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.History(tracksHistoryList))
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    fun onSearchQueryChanged(newQuery: String) {
        Log.d("SEARCH VM", "New query: $newQuery")

        _searchQuery.value = newQuery
        if (newQuery.isEmpty()) {
            latestSearchJob?.cancel()
        }
    }

    fun clearSearchQuery() {
        Log.d("SEARCH VM", "Query cleared")

        _searchQuery.value = ""
        latestSearchJob?.cancel()
        _searchScreenState.value = SearchState.History(tracksHistoryList)
    }

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            addToHistory(track)
        }
    }

    private fun addToHistory(track: Track) {
        tracksHistoryList.removeAll { it.trackId == track.trackId }

        tracksHistoryList.add(0, track)

        if (tracksHistoryList.size == 10) {
            tracksHistoryList.removeAt(tracksHistoryList.size - 1)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            trackHistoryInteractor.clearTrackHistory()
            tracksHistoryList.clear()
            _searchScreenState.value = SearchState.History(tracksHistoryList)
        }
    }

    fun retrySearch() {
        lastSearchedQuery?.let { query ->
            if (query.isNotEmpty()) {
                searchTracks(query)
            }
        }
    }

    fun searchTracks(query: String) {
        if (query.isBlank()) {
            return
        }

        lastSearchedQuery = query

        latestSearchJob?.cancel()
        latestSearchJob = viewModelScope.launch {
            _searchScreenState.value = SearchState.Loading
            trackInteractor
                .browseTracks(query)
                .collect {  result ->
                    result.onSuccess { foundTracks ->
                        if (foundTracks.isEmpty()) {
                            _searchScreenState.value = SearchState.Empty
                        } else {
                            _searchScreenState.value = SearchState.Success(foundTracks)
                        }
                    }.onFailure {
                        _searchScreenState.value = SearchState.Error
                    }
                }
        }
    }

    fun onFragmentStopped() {
        _searchScreenState.value = SearchState.History(tracksHistoryList)
    }
}