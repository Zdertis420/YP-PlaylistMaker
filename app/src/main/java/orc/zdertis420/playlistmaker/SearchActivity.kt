package orc.zdertis420.playlistmaker

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var backToMain: MaterialToolbar
    private lateinit var searchLine: EditText
    private lateinit var cancel: ImageView

    private lateinit var text: String

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

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

        searchLine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showCancelButton()
            }

            override fun afterTextChanged(s: Editable?) {
                text = searchLine.text.toString()

                browseTracks(text)
            }
        })

        val recycler = findViewById<RecyclerView>(R.id.tracks)

        recycler.layoutManager = LinearLayoutManager(this)
//        recycler.adapter = TrackAdapter(tracks = tracks)
    }

    @GET("/search?entity=song")
    private fun browseTracks(@Query("term") text: String) {
        val pmApiService = retrofit.create<PMApiService>()

        pmApiService.browseTracks(text).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {

                Log.i("SUCCESS", "there is a response")

                if (response.isSuccessful) {
                    val gson = Gson()

                    val tracksJson = response.body().toString()

                    val tracks = gson.fromJson(tracksJson, TrackResponse::class.java)

                    Log.w("Response type", "$tracks")

//                    val tracksResponse = gson.fromJson<>(tracksJson, TrackResponse::class.java)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("FAIL", "there's no response")
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_to_main -> {
                finishAffinity()
            }

            R.id.clear_text -> {
                searchLine.text.clear()
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("User input", text)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        searchLine.setText(savedInstanceState?.getString("User input"))
    }
}