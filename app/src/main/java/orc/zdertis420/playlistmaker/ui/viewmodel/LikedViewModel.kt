package orc.zdertis420.playlistmaker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import orc.zdertis420.playlistmaker.domain.interactor.TrackLikedInteractor
import orc.zdertis420.playlistmaker.ui.viewmodel.states.LikedState

class LikedViewModel(private val trackLikedInteractor: TrackLikedInteractor) : ViewModel() {

    private val _screenState = MutableStateFlow<LikedState>(LikedState.Loading)
    val screenState: StateFlow<LikedState> = _screenState

//    init {
//        observeLikedTracks()
//    }

    fun observeLikedTracks() {
        trackLikedInteractor.getLikedTracks()
            .onEach { tracks ->

                Log.d("TRACK", "Loading liked tracks. VM")

                if (tracks.isEmpty()) {
                    _screenState.value = LikedState.Empty
                } else {
                    _screenState.value = LikedState.LikedTracks(tracks)
                }
            }
            .catch { exception ->
                _screenState.value = LikedState.Error("Failed to load. $exception")
            }
            .launchIn(viewModelScope)
    }
}