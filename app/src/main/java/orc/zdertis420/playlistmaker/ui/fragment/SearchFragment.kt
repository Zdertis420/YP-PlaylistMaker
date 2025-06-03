package orc.zdertis420.playlistmaker.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.databinding.FragmentSearchBinding
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.adapter.TrackAdapter
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.SearchState
import orc.zdertis420.playlistmaker.utils.KeyboardUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val CLICK_DELAY = 1000L
    }

    private var _views: FragmentSearchBinding? = null
    private val views get() = _views!!

    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var searchQuery = ""

    private var tracksHistoryList = mutableListOf<Track>()

    private val keyboardUtil by inject<KeyboardUtil>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentSearchBinding.inflate(inflater, container, false)

        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchStateLiveData.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        setupTheme()

        setupRecyclers()

        setupListeners()
    }

    private fun setupTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            views.emptyResultImage.setImageResource(R.drawable.empty_result_dark)
            views.noConnectionImage.setImageResource(R.drawable.no_connection_dark)
        } else {
            views.emptyResultImage.setImageResource(R.drawable.empty_result_light)
            views.noConnectionImage.setImageResource(R.drawable.no_connection_light)
        }
    }

    private fun setupRecyclers() {
        with(views) {
            trackList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = TrackAdapter(emptyList())
            }

            tracksHistory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = TrackAdapter(tracksHistoryList)
            }
        }
    }

    private fun setupListeners() {
        views.clearText.setOnClickListener(this)

        views.updateConnection.setOnClickListener(this)

        views.clearHistory.setOnClickListener(this)

        views.searchLine.setOnFocusChangeListener { view, hasFocus ->
            viewModel.getTracksHistory()
        }

        views.searchLine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showClearButton()
                views.searchHistory.visibility =
                    if (views.searchLine.hasFocus() && s?.isEmpty() == true && tracksHistoryList.isNotEmpty()) View.VISIBLE else View.GONE

                if (s?.isNotEmpty() == true) {
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchQuery = views.searchLine.text.toString()

                if (s?.isEmpty() == true) {
                    Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                    showNothing()
                }
            }
        })

        views.searchLine.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchQuery.isNotEmpty()) {
                keyboardUtil.hideKeyboard(view)

                viewModel.searchTracks(searchQuery)
            }
            false
        }

        (views.trackList.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            val track =
                (viewModel.searchStateLiveData.value as SearchState.Success).tracks[position]

            addToHistory(track)

            startPlayer(track)
        }

        (views.tracksHistory.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            val track = tracksHistoryList[position]

            addToHistory(track)

            startPlayer(track)
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Empty -> showEmptyResultError()
            is SearchState.Error -> showNoConnectionError()
            is SearchState.Success -> showTracks(state.tracks)
            is SearchState.History -> showHistory(state.tracksHistory)
        }
    }

    private fun startPlayer(track: Track) {
        val args = bundleOf("track" to track.toDto())
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, args)
    }

    private fun addToHistory(track: Track) {
        Log.d("HISTORY BEFORE", tracksHistoryList.toString())

        tracksHistoryList.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }

        tracksHistoryList.add(0, track)

        if (tracksHistoryList.size == 10) {
            tracksHistoryList.removeAt(tracksHistoryList.size - 1)
        }

        Log.d("HISTORY AFTER", tracksHistoryList.toString())

        (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistoryList)

        viewModel.saveTracksHistory(tracksHistoryList)
    }

    private fun showTracks(tracks: List<Track>) {
        (views.trackList.adapter as TrackAdapter).updateTracks(tracks)

        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.VISIBLE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
        views.searchHistory.visibility = View.GONE
    }

    private fun showHistory(tracksHistory: List<Track>) {
        if (tracksHistory.isEmpty()) {
            showNothing()
            return
        }

        tracksHistoryList = tracksHistory.toMutableList()

        (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistoryList)

        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
        views.searchHistory.visibility = View.VISIBLE
    }

    private fun showEmptyResultError() {
        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.VISIBLE
        views.noConnection.visibility = View.GONE
        views.searchHistory.visibility = View.GONE
    }

    private fun showNoConnectionError() {
        Log.d("SWITCH", "HIDE TRACKS, SHOW NO CONNECTION ERROR")

        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.VISIBLE
        views.searchHistory.visibility = View.GONE
    }

    private fun showLoading() {
        views.progressBar.visibility = View.VISIBLE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
        views.searchHistory.visibility = View.GONE
    }

    private fun showNothing() {
        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.searchHistory.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
    }

    private fun showClearButton() {
        if (views.searchLine.text.isNotEmpty()) {
            views.clearText.alpha = 1.0F
        } else {
            views.clearText.alpha = 0.0F
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DELAY)
        }
        return current
    }

    override fun onClick(v: View?) {
        if (isClickAllowed) {
            clickDebounce()

            when (v?.id) {
                R.id.clear_text -> {
                    views.searchLine.text.clear()
                    searchQuery = ""

                    keyboardUtil.hideKeyboard(v)

                    Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                    viewModel.getTracksHistory()
                }

                R.id.update_connection -> {
                    Log.d("NO CONNECTION", "RETRY")

                    viewModel.searchTracks(searchQuery)
                }

                R.id.clear_history -> {
                    tracksHistoryList.clear()
                    viewModel.clearHistory()

                    (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistoryList)
                    showNothing()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _views = null
    }
}