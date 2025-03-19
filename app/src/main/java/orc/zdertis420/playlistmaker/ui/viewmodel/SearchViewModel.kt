package orc.zdertis420.playlistmaker.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Runnable
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor


class SearchViewModel(
    private val trackInteractor: TrackInteractor
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

        val tracks = trackInteractor.browseTracks(expression) { result ->
            result.onFailure { error ->
                _searchStateLiveData.value = SearchState.Error

                Log.e("ERROR", error.toString())
            }

            result.onSuccess { foundTracks ->
                if (foundTracks.isEmpty()) {
                    _searchStateLiveData.value = SearchState.Empty
                } else {
                    _searchStateLiveData.value = SearchState.Success(foundTracks)
                }
            }
        }
    }

    fun searchDebounce(querry: String) {
        searchQuery = querry

        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DELAY)
    }
}