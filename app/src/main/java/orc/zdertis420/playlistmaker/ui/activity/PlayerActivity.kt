package orc.zdertis420.playlistmaker.ui.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import orc.zdertis420.playlistmaker.Creator
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.ActivityPlayerBinding
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.viewmodel.PlayerViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val DELAY = 1000L
    }

    private lateinit var views: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    private var previewUrl = ""

    private val playerInteractor = Creator.providePlayerInteractor()

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

        views = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(views.root)


        views.back.setOnClickListener(this)

        views.playButton.setOnClickListener(this)

        views.timePlaying.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            intent.getParcelableExtra("track")
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(playerInteractor, track!!) as T
            }
        })[PlayerViewModel::class.java]

        viewModel.playerStateLiveData.observe(this) { state ->
            render(state)
        }

        loadTrack(track!!)

        viewModel.prepare()

        updateTimePLaying = object : Runnable {
            override fun run() {
                views.timePlaying.text = simpleDate.format(playerInteractor.getCurrentPosition())

                mainThreadHandler.postDelayed(this, DELAY)
            }
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Prepared -> startPlayer()
            is PlayerState.Play -> {}
            is PlayerState.Pause -> pausePlayer()
            is PlayerState.Error -> TODO()
        }
    }

    private fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }

        mainThreadHandler.post { updateTimePLaying.run() }
    }

    private fun startPlayer() {
        viewModel.start()

        views.playButton.setImageResource(R.drawable.pause_button)

        mainThreadHandler.post { updateTimePLaying.run() }
    }

    private fun pausePlayer() {
        playerInteractor.pause()

        views.playButton.setImageResource(R.drawable.play_button)

        mainThreadHandler.post { updateTimePLaying.run() }
    }

    private fun resetPlayer() {
        views.timePlaying.text = "0:00"
        mainThreadHandler.removeCallbacks(updateTimePLaying)
    }

    private fun loadTrack(track: Track) {

        Log.d("current", track.toString())

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .transform(RoundedCorners(8))
            .timeout(2500)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(views.trackImage)


        views.trackName.text = track.trackName
        views.trackAuthor.text = track.artistName
        views.country.text = track.country
        views.genre.text = track.primaryGenreName
        views.year.text = track.releaseDate.substring(0, 4)
        views.album.text = track.collectionName
        views.duration.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)
        previewUrl = track.previewUrl



        views.trackName.isSelected = true
        views.trackAuthor.isSelected = true
        views.country.isSelected = true
        views.genre.isSelected = true
        views.album.isSelected = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> finish()

            R.id.play_button -> playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.release()
    }
}