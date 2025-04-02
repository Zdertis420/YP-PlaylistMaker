package orc.zdertis420.playlistmaker.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import orc.zdertis420.playlistmaker.Creator
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.ActivitySearchBinding
import orc.zdertis420.playlistmaker.ui.track.TrackAdapter
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.viewmodel.states.SearchState
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel
import orc.zdertis420.playlistmaker.utils.KeyboardUtil
import orc.zdertis420.playlistmaker.utils.NetworkUtil


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val CLICK_DELAY = 1000L
    }

    private lateinit var views: ActivitySearchBinding

    private lateinit var viewModel: SearchViewModel

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var searchQuery = ""

    private var tracksHistoryList = mutableListOf<Track>()

    private val trackInteractor = Creator.provideTrackInteractor()
    private val trackHistoryInteractor = Creator.provideTrackHistoryInteractor(this)
    private val keyboardUtil = KeyboardUtil(this)
    private val networkUtil = NetworkUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        views = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(views.root)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(trackInteractor, trackHistoryInteractor) as T
            }
        })[SearchViewModel::class.java]

        viewModel.searchStateLiveData.observe(this) { state ->
            render(state)
        }

        setupTheme()

        setupRecyclers()

        setupListeners()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        views.searchLine.setText(savedInstanceState?.getString("User input") ?: "")
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
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = TrackAdapter(emptyList())
            }

            tracksHistory.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = TrackAdapter(tracksHistoryList)
            }
        }
    }

    private fun setupListeners() {
        views.backToMain.setOnClickListener(this)
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

                if (!networkUtil.isNetworkAvailable()) {
                    showNoConnectionError()
                    return
                }

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

                if (!networkUtil.isNetworkAvailable()) {
                    showNoConnectionError()
                    return@setOnEditorActionListener false
                }

                viewModel.searchTracks(searchQuery)
            }
            false
        }

        (views.trackList.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            val track = (viewModel.searchStateLiveData.value as SearchState.Success).tracks[position]

            addToHistory(track)

            startPlayerActivity(track)
        }

        (views.tracksHistory.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            val track = (viewModel.searchStateLiveData.value as SearchState.History).tracksHistory[position]

            addToHistory(track)

            startPlayerActivity(track)
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

    private fun startPlayerActivity(track: Track) {
        val startPlayerActivity = Intent(this, PlayerActivity::class.java)

        startPlayerActivity.putExtra("track", track)

        startActivity(startPlayerActivity)
    }

    private fun addToHistory(track: Track) {
        Log.d("HISTORY BEFORE", tracksHistoryList.toString())

        if (tracksHistoryList.contains(track)) {
            tracksHistoryList.remove(track)
        }

        if (tracksHistoryList.size == 10) {
            tracksHistoryList.removeAt(9)
        }

        tracksHistoryList.add(0, track)

        Log.d("HISTORY AFTER", tracksHistoryList.toString())

        (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistoryList)
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
                R.id.back_to_main -> finish()

                R.id.clear_text -> {
                    views.searchLine.text.clear()
                    searchQuery = ""

                    keyboardUtil.hideKeyboard(v)

                    Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                    showNothing()
                }

                R.id.update_connection -> {
                    Log.d("NO CONNECTION", "RETRY")

                    if (!networkUtil.isNetworkAvailable()) {
                        showNoConnectionError()
                        return
                    }

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

    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        outState.putString("User input", searchQuery)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onPause() {
        super.onPause()

        viewModel.saveTracksHistory(tracksHistoryList)
    }
}