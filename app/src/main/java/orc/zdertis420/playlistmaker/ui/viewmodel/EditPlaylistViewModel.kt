package orc.zdertis420.playlistmaker.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.domain.interactor.CreatePlaylistInteractor
import orc.zdertis420.playlistmaker.domain.usecase.SaveImageUseCase

class EditPlaylistViewModel(
    private val createPlaylistInteractor: CreatePlaylistInteractor,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _playlistEditingState = MutableStateFlow<Result<Unit>?>(null)
    val playlistEditingState: StateFlow<Result<Unit>?> = _playlistEditingState

    fun onImageSelected(uri: Uri) {
        Log.d("PLAYLIST VM", "Uri selected: $uri")
        _selectedImageUri.value = uri
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            try {
                val localPath = _selectedImageUri.value?.let { uri ->
                    saveImageUseCase.invoke(uri)
                }

                createPlaylistInteractor.createPlaylist(name, description, localPath.toString())

                _playlistEditingState.value = Result.success(Unit)
            } catch (e: Exception) {
                _playlistEditingState.value = Result.failure(e)
            }
        }
    }

    fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String?,
    ) {
        viewModelScope.launch {
            try {
                Log.d("PLAYLIST", "New uri to save ${_selectedImageUri.value}")
                val localPath = _selectedImageUri.value?.let { uri ->
                    Log.d("PLAYLIST", "Saving new uri: $uri")
                    saveImageUseCase.invoke(uri)
                }

                Log.d("PLAYLIST", "Uri to update playlist: $localPath")

                createPlaylistInteractor.updatePlaylist(
                    playlistId = playlistId,
                    name = name,
                    description = description,
                    coverImagePath = localPath
                )

                _playlistEditingState.value = Result.success(Unit)
            } catch (e: Exception) {
                _playlistEditingState.value = Result.failure(e)
            }
        }
    }
}