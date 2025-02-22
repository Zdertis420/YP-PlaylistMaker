package orc.zdertis420.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import orc.zdertis420.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class PlayerActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 1000L
    }

    private lateinit var back: MaterialToolbar

    private lateinit var trackImage: ShapeableImageView
    private lateinit var trackName: TextView
    private lateinit var trackAuthor: TextView
    private lateinit var saveToLibrary: ImageView
    private lateinit var playButton: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var timePlaying: TextView
    private lateinit var country: TextView
    private lateinit var genre: TextView
    private lateinit var year: TextView
    private lateinit var album: TextView
    private lateinit var duration: TextView
    private var previewUrl: String = ""

    private lateinit var tracks: List<Track>
    private var current by Delegates.notNull<Int>()

    private var mediaPlayer = MediaPlayer()
    private var state = STATE_DEFAULT

    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimePLaying: Runnable
    private val simpleDate by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        back = findViewById(R.id.back)

        trackImage = findViewById(R.id.track_image)
        trackName = findViewById(R.id.track_name)
        trackAuthor = findViewById(R.id.track_author)
        saveToLibrary = findViewById(R.id.save_to_library)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)
        timePlaying = findViewById(R.id.time_playing)
        country = findViewById(R.id.country)
        genre = findViewById(R.id.genre)
        year = findViewById(R.id.year)
        album = findViewById(R.id.album)
        duration = findViewById(R.id.duration)

        back.setOnClickListener(this)

        playButton.setOnClickListener(this)

        timePlaying.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)

        val extras = intent.extras

        tracks = Gson().fromJson(
            extras!!.getString("tracks"),
            object : TypeToken<MutableList<Track>>() {}.type
        )

        current = extras.getInt("current track")

        updateTrack(current)

        preparePlayer()

        updateTimePLaying = object : Runnable {
            override fun run() {
                timePlaying.text = simpleDate.format(mediaPlayer.currentPosition)

                mainThreadHandler.postDelayed(this, DELAY)
            }
        }
    }

    private fun preparePlayer() {
        try {
            mediaPlayer.setDataSource(previewUrl)
        } catch (npe: NullPointerException) {
            Toast.makeText(this, getString(R.string.no_preview), Toast.LENGTH_SHORT).show()

            Log.e("APPLE", "OFFICE OF FAGGOTS")

            return
        }

        mediaPlayer.setOnPreparedListener {
            state = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            stopPlayer()
        }

        mediaPlayer.prepareAsync()
    }

    private fun startPlayer() {
        mediaPlayer.start()

        playButton.setImageResource(R.drawable.pause_button)

        state = STATE_PLAYING

        mainThreadHandler.post { updateTimePLaying.run() }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()

        playButton.setImageResource(R.drawable.play_button)

        state = STATE_PAUSED

        mainThreadHandler.post { updateTimePLaying.run() }
    }

    private fun stopPlayer() {
        mediaPlayer.stop()

        playButton.setImageResource(R.drawable.play_button)
        timePlaying.text = "0:00"

        state = STATE_PREPARED

        mainThreadHandler.removeCallbacks(updateTimePLaying)
    }

    private fun playbackControl() {
        mainThreadHandler.post { updateTimePLaying.run() }

        when (state) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun updateTrack(current: Int) {
        val track = tracks[current]

        Log.d("current", track.toString())

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .transform(RoundedCorners(8))
            .timeout(2500)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(trackImage)


        trackName.text = track.trackName
        trackAuthor.text = track.artistName
        country.text = track.country
        genre.text = track.primaryGenreName
        year.text = track.releaseDate.substring(0, 4)
        album.text = track.collectionName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)
        previewUrl = track.previewUrl



        trackName.isSelected = true
        trackAuthor.isSelected = true
        country.isSelected = true
        genre.isSelected = true
        album.isSelected = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> finish()

            R.id.play_button -> {
                playbackControl()


            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}