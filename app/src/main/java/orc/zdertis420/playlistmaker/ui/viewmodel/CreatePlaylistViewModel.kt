package orc.zdertis420.playlistmaker.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.domain.usecase.CreatePlaylistUseCase
import orc.zdertis420.playlistmaker.domain.usecase.SaveImageUseCase

class CreatePlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _playlistCreationState = MutableStateFlow<Result<Unit>?>(null)
    val playlistCreationState: StateFlow<Result<Unit>?> = _playlistCreationState

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            try {
                val localPath = _selectedImageUri.value?.let { uri ->
                    saveImageUseCase.invoke(uri)
                }

                createPlaylistUseCase.createPlaylist(name, description, localPath.toString())

                _playlistCreationState.value = Result.success(Unit)
            } catch (e: Exception) {
                _playlistCreationState.value = Result.failure(e)
            }
        }
    }
}