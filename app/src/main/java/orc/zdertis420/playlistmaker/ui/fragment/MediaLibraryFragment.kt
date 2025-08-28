package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.ui.compose.MediaLibraryScreen
import orc.zdertis420.playlistmaker.ui.viewmodel.LikedViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val likedViewModel: LikedViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycleScope.launch {
            likedViewModel.observeLikedTracks()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            playlistsViewModel.loadPlaylists()
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val likedState by likedViewModel.screenState.collectAsState()
                val playlistsState by playlistsViewModel.playlistStateFlow.collectAsState()

                MediaLibraryScreen(
                    likedState = likedState,
                    playlistsState = playlistsState,
                    onTrackClicked = { track ->
                        val args = bundleOf("track" to track.toDto())
                        findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment, args)
                    },
                    onPlaylistClicked = { playlist ->
                        val args = bundleOf("playlist" to playlist.toDto())
                        findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistFragment, args)
                    },
                    onNewPlaylistClicked = {
                        findNavController().navigate(R.id.action_mediaLibraryFragment_to_editPlaylistFragment)
                    }
                )
            }
        }
    }
}
