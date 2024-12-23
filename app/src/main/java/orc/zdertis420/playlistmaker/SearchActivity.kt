package orc.zdertis420.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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

    private lateinit var searchQuery: String

    private lateinit var recycler: RecyclerView

    private lateinit var emptyResult: LinearLayout
    private lateinit var emptyResultImage: ImageView

    private lateinit var noConnection: LinearLayout
    private lateinit var noConnectionImage: ImageView
    private lateinit var updateConnection: Button

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1000, TimeUnit.MILLISECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private var tracks: List<Track> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind()
    }

    private fun bind() {
        backToMain = findViewById(R.id.back_to_main)
        searchLine = findViewById(R.id.search_line)
        cancel = findViewById(R.id.clear_text)

        backToMain.setOnClickListener(this@SearchActivity)
        cancel.setOnClickListener(this@SearchActivity)

        emptyResult = findViewById(R.id.empty_result)
        emptyResultImage = findViewById(R.id.empty_result_image)

        noConnection = findViewById(R.id.no_connection)
        noConnectionImage = findViewById(R.id.no_connection_image)
        updateConnection = findViewById(R.id.update_connection)

        updateConnection.setOnClickListener(this@SearchActivity)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            emptyResultImage.setImageResource(R.drawable.empty_result_dark)
            noConnectionImage.setImageResource(R.drawable.no_connection_dark)
        } else {
            emptyResultImage.setImageResource(R.drawable.empty_result_light)
            noConnectionImage.setImageResource(R.drawable.no_connection_light)
        }

        searchLine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showCancelButton()
            }

            override fun afterTextChanged(s: Editable?) {
                searchQuery = searchLine.text.toString()
            }
        })

        searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                browseTracks(searchQuery)
            }
            false
        }

        recycler = findViewById(R.id.tracks)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(tracks = tracks)
    }

    private fun checkNetworkAvailability(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (
            (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    )?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) != false
        ) {
            TODO("FOR FUTURE")
        } else {
            TODO("FOR FUTURE")
        }
    }

    @GET("/search?q=term")
    private fun browseTracks(@Query("term") text: String) {
        val pmApiService = retrofit.create<PMApiService>()
        val tracks = mutableListOf<Track>()

        pmApiService.browseTracks(text).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {

                Log.i("SUCCESS", "there is a response for $text")

                if (response.isSuccessful) {
                    val tracksJson = response.body()

                    Log.i("RESPONSE", tracksJson.toString())

                    if (tracksJson!!.resultCount == 0 && text != "") {
                        Log.d("EMPTY RESULT", "EMPTY RESULT FOR $text")

                        changeContent()
                        return
                    }

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

                    changeContent()

                    (recycler.adapter as TrackAdapter).updateTracks(tracks)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("FAIL", "there's no response")

                changeContent()
            }
        })
    }

    private fun showCancelButton() {
        if (searchLine.text.isNotEmpty()) {
            cancel.alpha = 1.0F
        } else {
            cancel.alpha = 0.0F
        }
    }

    private fun changeContent() {
        val stackTrace = Thread.currentThread().stackTrace

        if (stackTrace.size >= 3) {
            when (stackTrace[4].lineNumber) {
                166 -> {
                    Log.d("SWITCH", "HIDE TRACKS, SHOW EMPTY RESULT ERROR")

                    recycler.visibility = View.GONE
                    emptyResult.visibility = View.VISIBLE
                    noConnection.visibility = View.GONE
                }

                194 -> {
                    Log.d("SWITCH", "HIDE ERRORS, SHOW TRACKS")

                    recycler.visibility = View.VISIBLE
                    emptyResult.visibility = View.GONE
                    noConnection.visibility = View.GONE
                }

                203 -> {
                    Log.d("SWITCH", "HIDE TRACKS, SHOW NO CONNECTION ERROR")

                    recycler.visibility = View.GONE
                    emptyResult.visibility = View.GONE
                    noConnection.visibility = View.VISIBLE
                }

                else -> {
                    if (stackTrace[3].toString() == "orc.zdertis420.playlistmaker.SearchActivity.onClick(SearchActivity.kt:267)") {
                        Log.d("SWITCH", "HIDE EVERYTHING, QUERY CLEARED")

                        recycler.visibility = View.GONE
                        emptyResult.visibility = View.GONE
                        noConnection.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_to_main -> finish()

            R.id.clear_text -> {
                searchLine.text.clear()
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, 0)

                changeContent()
            }

            R.id.update_connection -> {
                browseTracks(searchQuery)

                Log.d("NO CONNECTION", "RETRY")
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

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        searchLine.setText(savedInstanceState?.getString("User input"))
    }
}