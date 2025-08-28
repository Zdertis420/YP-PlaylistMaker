package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.compose.SearchScreen
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.searchScreenState.collectAsStateWithLifecycle()

                SearchScreen(
                    state = state,
                    onSearchQueryChanged = { newQuery ->
                        viewModel.onSearchQueryChanged(newQuery)
                    },
                    onSearchClicked = { query ->
                        viewModel.searchTracks(query)
                    },
                    onClearSearchClicked = {
                        viewModel.clearSearchQuery()
                    },
                    onTrackClicked = { track ->
                        viewModel.onTrackClicked(track)
                        navigateToPlayer(track)
                    },
                    onClearHistoryClicked = {
                        viewModel.clearHistory()
                    },
                    onRefreshClicked = {
                        viewModel.retrySearch()
                    },
                )
            }
        }
    }

    private fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            Bundle().apply {
                putParcelable("track", track.toDto())
            }
        )
    }

    override fun onStop() {
        super.onStop()
        viewModel.onFragmentStopped()
    }
}