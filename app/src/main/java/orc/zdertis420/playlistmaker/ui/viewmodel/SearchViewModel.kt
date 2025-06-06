package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _searchStateLiveData = MutableLiveData<SearchState>()
    val searchStateLiveData: LiveData<SearchState> get() = _searchStateLiveData

    private var searchQuery = ""

    private var searchJob: Job? = null

    fun searchTracks(expression: String) {
        Log.d("SEARCH", "Started search with query: $expression")

        _searchStateLiveData.value = SearchState.Loading

        Log.d("SEARCH", "Network availability: ${networkUtil.isNetworkAvailable()}")
        if (!networkUtil.isNetworkAvailable()) {
            _searchStateLiveData.value = SearchState.Error
            return
        }

        viewModelScope.launch {
            trackInteractor.browseTracks(expression)
                .collect { result ->
                    result.onSuccess { foundTracks ->
                        if (foundTracks.isEmpty()) {
                            Log.d("SEARCH", "Result is empty")
                            _searchStateLiveData.value = SearchState.Empty
                        } else {
                            Log.d("SEARCH", "Result is $foundTracks")
                            _searchStateLiveData.value = SearchState.Success(foundTracks)
                        }
                    }.onFailure {
                        Log.d("SEARCH", "Search ended with error: $result")
                        _searchStateLiveData.value = SearchState.Error
                    }
                }
        }
    }

    fun searchDebounce(query: String) {
        searchQuery = query

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            searchTracks(searchQuery)
        }
    }

    fun cancelSearch() {
        searchJob?.cancel()
    }

    fun getTracksHistory() {
        val history = trackHistoryInteractor.getTrackHistory()

        _searchStateLiveData.value = SearchState.History(history)
    }

    fun saveTracksHistory(tracksHistory: MutableList<Track>) {
        trackHistoryInteractor.saveTrackHistory(tracksHistory)
    }

    fun clearHistory() {
        trackHistoryInteractor.clearTrackHistory()
        _searchStateLiveData.value = SearchState.History(emptyList())
    }
}