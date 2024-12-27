package orc.zdertis420.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var backToMain: MaterialToolbar
    private lateinit var searchLine: EditText
    private lateinit var cancel: ImageView

    private var searchQuery = ""

    private lateinit var tracksList: RecyclerView

    private lateinit var searchHistory: LinearLayout
    private lateinit var tracksHistoryView: RecyclerView
    private lateinit var clearHistory: TextView

    private lateinit var emptyResult: LinearLayout
    private lateinit var emptyResultImage: ImageView

    private lateinit var noConnection: LinearLayout
    private lateinit var noConnectionImage: ImageView
    private lateinit var updateConnection: TextView

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1000, TimeUnit.MILLISECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private var tracks = mutableListOf<Track>()

    private var tracksHistory = mutableListOf<Track>()

    private val history by lazy {
        application.getSharedPreferences("HISTORY", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backToMain = findViewById(R.id.back_to_main)
        searchLine = findViewById(R.id.search_line)
        cancel = findViewById(R.id.clear_text)

        backToMain.setOnClickListener(this)
        cancel.setOnClickListener(this)

        emptyResult = findViewById(R.id.empty_result)
        emptyResultImage = findViewById(R.id.empty_result_image)

        noConnection = findViewById(R.id.no_connection)
        noConnectionImage = findViewById(R.id.no_connection_image)
        updateConnection = findViewById(R.id.update_connection)

        updateConnection.setOnClickListener(this)

        searchHistory = findViewById(R.id.search_history)
        tracksHistoryView = findViewById(R.id.tracks_history)
        clearHistory = findViewById(R.id.clear_history)

        clearHistory.setOnClickListener(this)

        tracksHistoryView.layoutManager = LinearLayoutManager(this)
        tracksHistoryView.adapter = TrackAdapter(tracksHistory.reversed())
        tracksList = findViewById(R.id.tracks)

        tracksList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = TrackAdapter(tracks)

        bind()
    }

    override fun onStart() {
        super.onStart()

        tracksHistory = Gson().fromJson(
            history.getString("HISTORY", ""),
            object : TypeToken<MutableList<Track>>() {}.type
        ) ?: mutableListOf()

        (tracksHistoryView.adapter as TrackAdapter).updateTracks(tracksHistory.reversed())
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        searchLine.setText(savedInstanceState?.getString("User input"))
    }

    private fun bind() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            emptyResultImage.setImageResource(R.drawable.empty_result_dark)
            noConnectionImage.setImageResource(R.drawable.no_connection_dark)
        } else {
            emptyResultImage.setImageResource(R.drawable.empty_result_light)
            noConnectionImage.setImageResource(R.drawable.no_connection_light)
        }

        searchLine.setOnFocusChangeListener { view, hasFocus ->
            searchHistory.visibility =
                if (hasFocus && searchLine.text.isEmpty() && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }

        searchLine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showClearButton()
                searchHistory.visibility =
                    if (searchLine.hasFocus() && s?.isEmpty() == true && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                searchQuery = searchLine.text.toString()

                if (s?.isEmpty() == true) {
                    Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                    tracksList.visibility = View.GONE
                    emptyResult.visibility = View.GONE
                    noConnection.visibility = View.GONE
                }
            }
        })

        searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchQuery.isNotEmpty()) {
                browseTracks(searchQuery)
            }
            false
        }

        (tracksList.adapter as TrackAdapter).setOnItemClickListener { position: Int ->
            Log.d("TRACK CLICKED", "TRACK ${tracks[position].trackName} CLICKED")

            if (tracksHistory.contains(tracks[position])) {
                tracksHistory.remove(tracks[position])
            }

            addToHistory(tracks[position])

            Log.d(
                "ADDED TO HISTORY",
                "${tracks[position].trackName} is ${tracksHistory.indexOf(tracks[position])}"
            )

            (tracksHistoryView.adapter as TrackAdapter).updateTracks(tracksHistory.reversed())
        }
    }

    private fun addToHistory(track: Track) {
        if (tracksHistory.size == 10) {
            tracksHistory.removeAt(0)
        }
        tracksHistory.add(track)
    }

    @GET("/search?q=term")
    private fun browseTracks(@Query("term") text: String) {
        val pmApiService = retrofit.create<PMApiService>()

        pmApiService.browseTracks(text).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {

                Log.i("SUCCESS", "there is a response for $text")

                if (response.isSuccessful) {
                    val tracksJson = response.body()

                    Log.i("RESPONSE", tracksJson.toString())

                    if (tracksJson!!.resultCount == 0 && text != "") {
                        Log.d("EMPTY RESULT", "EMPTY RESULT FOR $text")

                        Log.d("SWITCH", "HIDE TRACKS, SHOW EMPTY RESULT ERROR")

                        tracksList.visibility = View.GONE
                        emptyResult.visibility = View.VISIBLE
                        noConnection.visibility = View.GONE

                        return
                    }

                    tracks.clear()

                    for (i in 0..<tracksJson.resultCount) {
                        try {
                            tracks.add(
                                Track(
                                    tracksJson.results[i].trackName,
                                    tracksJson.results[i].artistName,
                                    tracksJson.results[i].trackTimeMillis,
                                    tracksJson.results[i].artworkUrl100
                                )
                            )
                        } catch (npe: NullPointerException) {
                            Log.d("NULL", "SKIP THAT MF")
                            continue
                        }

                        Log.i(
                            "MEGA SUCCESS",
                            "TRACK ${tracksJson.results[i].trackName} - ${tracksJson.results[i].artistName} ADDED"
                        )
                    }

                    Log.i("CRITICAL SUCCESS", "ALL TRACKS ADDED")
                    Log.i("TRACKS", "List of all added tracks: $tracks")

                    Log.d("SWITCH", "HIDE ERRORS, SHOW TRACKS")

                    tracksList.visibility = View.VISIBLE
                    emptyResult.visibility = View.GONE
                    noConnection.visibility = View.GONE

                    (tracksList.adapter as TrackAdapter).updateTracks(tracks)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("FAIL", "there's no response")

                Log.d("SWITCH", "HIDE TRACKS, SHOW NO CONNECTION ERROR")

                tracksList.visibility = View.GONE
                emptyResult.visibility = View.GONE
                noConnection.visibility = View.VISIBLE
            }
        })
    }

    private fun showClearButton() {
        if (searchLine.text.isNotEmpty()) {
            cancel.alpha = 1.0F
        } else {
            cancel.alpha = 0.0F
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_to_main -> finish()

            R.id.clear_text -> {
                searchLine.text.clear()
                searchQuery = ""
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, 0)

                Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                tracksList.visibility = View.GONE
                emptyResult.visibility = View.GONE
                noConnection.visibility = View.GONE
            }

            R.id.update_connection -> {
                browseTracks(searchQuery)

                Log.d("NO CONNECTION", "RETRY")
            }

            R.id.clear_history -> {
                tracksHistory.clear()
                history.edit()
                    .clear()
                    .apply()

                (tracksHistoryView.adapter as TrackAdapter).updateTracks(tracksHistory)

                searchHistory.visibility = View.GONE
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
        history.edit()
            .putString("HISTORY", Gson().toJson(tracksHistory).toString())
            .apply()
    }
}