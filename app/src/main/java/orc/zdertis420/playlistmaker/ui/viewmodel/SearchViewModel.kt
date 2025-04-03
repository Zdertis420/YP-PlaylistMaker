package orc.zdertis420.playlistmaker.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Runnable
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
        private const val SEARCH_DELAY = 1500L
    }

    private val _searchStateLiveData = MutableLiveData<SearchState>()
    val searchStateLiveData: LiveData<SearchState> get() = _searchStateLiveData

    private var searchQuery = ""

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        searchTracks(searchQuery)
    }

    fun searchTracks(expression: String) {
        _searchStateLiveData.value = SearchState.Loading

        if (!networkUtil.isNetworkAvailable()) {
            _searchStateLiveData.postValue(SearchState.Error)
            return
        }

        trackInteractor.browseTracks(expression) { result ->
            result.onFailure { error ->
                _searchStateLiveData.postValue(SearchState.Error)

                Log.e("ERROR", error.toString())
            }

            result.onSuccess { foundTracks ->
                if (foundTracks.isEmpty()) {
                    _searchStateLiveData.postValue(SearchState.Empty)
                } else {
                    _searchStateLiveData.postValue(SearchState.Success(foundTracks))
                }
            }
        }
    }

    fun searchDebounce(query: String) {
        searchQuery = query

        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DELAY)
    }

    fun getTracksHistory() {
        Log.d("GET", "HISTORY")
        val history = trackHistoryInteractor.getTrackHistory()

        _searchStateLiveData.postValue(SearchState.History(history))
    }

    fun saveTracksHistory(tracksHistory: MutableList<Track>) {
        trackHistoryInteractor.saveTrackHistory(tracksHistory)
    }

    fun clearHistory() {
        trackHistoryInteractor.clearTrackHistory()
        _searchStateLiveData.postValue(SearchState.History(emptyList()))
    }
}