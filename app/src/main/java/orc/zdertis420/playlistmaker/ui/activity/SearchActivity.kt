package orc.zdertis420.playlistmaker.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchState
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val CLICK_DELAY = 1000L
    }

    private lateinit var views: ActivitySearchBinding

    private lateinit var viewModel: SearchViewModel

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var searchQuery = ""

    private var tracks = mutableListOf<Track>()

    private var tracksHistory = mutableListOf<Track>()

    private val trackInteractor = Creator.provideTrackInteractor()
    private val trackHistoryInteractor = Creator.provideTrackHistoryInteractor(this)
    private val keyboardUtil = Creator.provideKeyboardUtil(this)
    private val networkUtil = Creator.provideNetworkUtil(this)

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
                return SearchViewModel(trackInteractor) as T
            }
        })[SearchViewModel::class.java]

        viewModel.searchStateLiveData.observe(this) { state ->
            render(state)
        }

        views.backToMain.setOnClickListener(this)
        views.clearText.setOnClickListener(this)

        views.updateConnection.setOnClickListener(this)

        views.clearHistory.setOnClickListener(this)

        views.trackList.layoutManager = LinearLayoutManager(this)
        views.trackList.adapter = TrackAdapter(tracks)

        views.tracksHistory.layoutManager = LinearLayoutManager(this)
        views.tracksHistory.adapter = TrackAdapter(tracksHistory.reversed())

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            views.emptyResultImage.setImageResource(R.drawable.empty_result_dark)
            views.noConnectionImage.setImageResource(R.drawable.no_connection_dark)
        } else {
            views.emptyResultImage.setImageResource(R.drawable.empty_result_light)
            views.noConnectionImage.setImageResource(R.drawable.no_connection_light)
        }

        views.searchLine.setOnFocusChangeListener { view, hasFocus ->
            views.searchHistory.visibility =
                if (hasFocus && views.searchLine.text.isEmpty() && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }

        views.searchLine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showClearButton()
                views.searchHistory.visibility =
                    if (views.searchLine.hasFocus() && s?.isEmpty() == true && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE

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

        views.searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchQuery.isNotEmpty()) {
                hideKeyboard()

                viewModel.searchTracks(searchQuery)
            }
            false
        }

        (views.trackList.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            addToHistory(tracks[position])

            startPlayerActivity(tracks[position])

        }

        (views.tracksHistory.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            startPlayerActivity(tracksHistory.reversed()[position])

            addToHistory(tracksHistory.reversed()[position])

        }
    }

    override fun onStart() {
        super.onStart()

        tracksHistory = trackHistoryInteractor.getTrackHistory()

        (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistory.reversed())
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        views.searchLine.setText(savedInstanceState?.getString("User input"))
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Empty -> showEmptyResultError()
            is SearchState.Error -> showNoConnectionError()
            is SearchState.Success -> showTracks(state.tracks)
        }
    }

    private fun startPlayerActivity(track: Track) {
        val startPlayerActivity = Intent(this, PlayerActivity::class.java)

        startPlayerActivity.putExtra("track", track)

        startActivity(startPlayerActivity)
    }

    private fun addToHistory(track: Track) {
        if (tracksHistory.contains(track)) {
            tracksHistory.remove(track)
        }

        if (tracksHistory.size == 10) {
            tracksHistory.removeAt(0)
        }
        tracksHistory.add(track)

        (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistory.reversed())

    }

    private fun showTracks(tracks: List<Track>) {
        (views.trackList.adapter as TrackAdapter).updateTracks(tracks)

        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.VISIBLE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
    }

    private fun showEmptyResultError() {
        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.VISIBLE
        views.noConnection.visibility = View.GONE
    }

    private fun showNoConnectionError() {
        Log.d("SWITCH", "HIDE TRACKS, SHOW NO CONNECTION ERROR")

        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.VISIBLE
    }

    private fun showLoading() {
        views.progressBar.visibility = View.VISIBLE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
    }

    private fun showNothing() {
        views.progressBar.visibility = View.GONE
        views.trackList.visibility = View.GONE
        views.emptyResult.visibility = View.GONE
        views.noConnection.visibility = View.GONE
    }

    private fun networkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showClearButton() {
        if (views.searchLine.text.isNotEmpty()) {
            views.clearText.alpha = 1.0F
        } else {
            views.clearText.alpha = 0.0F
        }
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(views.searchLine.windowToken, 0)
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

                    hideKeyboard()

                    Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                    showNothing()
                }

                R.id.update_connection -> {
                    viewModel.searchTracks(searchQuery)

                    Log.d("NO CONNECTION", "RETRY")
                }

                R.id.clear_history -> {
                    tracksHistory.clear()
                    trackHistoryInteractor.clearTrackHistory()

                    (views.tracksHistory.adapter as TrackAdapter).updateTracks(tracksHistory)

                    views.searchHistory.visibility = View.GONE
                }
            }
        }
    }

    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("User input", searchQuery)
    }

    override fun onStop() {
        super.onStop()

        val trackHistoryInteractor = Creator.provideTrackHistoryInteractor(application)

        trackHistoryInteractor.saveTrackHistory(tracksHistory)
    }
}