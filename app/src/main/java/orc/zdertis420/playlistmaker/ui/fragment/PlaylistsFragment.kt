package orc.zdertis420.playlistmaker.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.databinding.FragmentPlaylistsBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.ui.adapter.playlist.LibraryPlaylistAdapter
import orc.zdertis420.playlistmaker.ui.adapter.playlist.PlayerPlaylistAdapter
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistsViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _views: FragmentPlaylistsBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureDark()

        views.createPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_editPlaylistFragment)
        }

        views.playlists.adapter = LibraryPlaylistAdapter(emptyList())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.playlistStateFlow.collect { state ->
                    render(state)
                }
            }
        }

        (views.playlists.adapter as LibraryPlaylistAdapter).setOnItemClickListener { position ->
            val playlist = (viewModel.playlistStateFlow.value as PlaylistsState.Playlists).playlists[position]
            val args = bundleOf("playlist" to playlist.toDto())
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistFragment, args)
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showEmpty()
            PlaylistsState.Error -> Toast.makeText(
                requireActivity(), getString(R.string.loading_error),
                Toast.LENGTH_SHORT
            ).show()

            is PlaylistsState.Playlists -> showPlaylists(state.playlists)
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        Log.d("PLAYLISTS", playlists.toString())

        (views.playlists.adapter as LibraryPlaylistAdapter).updatePlaylists(playlists)

        views.noPlaylists.visibility = View.GONE
        views.playlists.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        views.playlists.visibility = View.GONE
        views.noPlaylists.visibility = View.VISIBLE
    }

    private fun configureDark() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            views.noPlaylistsImg.setImageResource(R.drawable.empty_result_dark)
        } else {
            views.noPlaylistsImg.setImageResource(R.drawable.empty_result_light)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}
