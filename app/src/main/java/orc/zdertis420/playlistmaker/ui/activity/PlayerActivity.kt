package orc.zdertis420.playlistmaker.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.dto.TrackDto
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.databinding.ActivityPlayerBinding
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.viewmodel.PlayerViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var views: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel>()

    private lateinit var track: Track
    private var previewUrl = ""

    private val simpleDate by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        views = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(views.root)
        ViewCompat.setOnApplyWindowInsetsListener(views.playerActivity) { view, windowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        views.playButton.isEnabled = false

        views.back.setOnClickListener(this)
        views.playButton.setOnClickListener(this)
        views.likeButton.setOnClickListener(this)

        views.timePlaying.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", TrackDto::class.java)
        } else {
            intent.getParcelableExtra("track")
        }!!.toTrack()

        viewModel.setTrack(track)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerStateFlow.collect { state ->
                    render(state)
                }
            }
        }

        loadTrack(track)

        Log.d("THREAD", Thread.currentThread().toString())

        viewModel.prepare()
    }

    private fun render(state: PlayerState) {
        Log.d("STATE", state.toString())

        when (state) {
            is PlayerState.Prepared -> showPrepared()
            is PlayerState.Play -> showPlaying(state.remainingMillis)
            is PlayerState.Pause -> showPause()
            is PlayerState.Error -> {
                Log.e("ERROR", state.msg)
                Toast.makeText(this, state.msg, Toast.LENGTH_SHORT).show()
            }

            PlayerState.Preparing -> Toast.makeText(this, R.string.preparing, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPrepared() {
        views.playButton.isEnabled = true
        views.timePlaying.text = "00:00"
    }

    private fun showPlaying(remainingMillis: Long) {
        views.playButton.setImageResource(R.drawable.pause_button)

        views.timePlaying.text = simpleDate.format(remainingMillis)
    }

    private fun showPause() {
        views.playButton.setImageResource(R.drawable.play_button)
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

        if (track.isLiked) {
            views.likeButton.setImageResource(R.drawable.like_button_active)
        }



        views.trackName.isSelected = true
        views.trackAuthor.isSelected = true
        views.country.isSelected = true
        views.genre.isSelected = true
        views.album.isSelected = true
    }

    private fun toggleLikeView() {
        if (track.isLiked) {
            track.isLiked = false
            views.likeButton.setImageResource(R.drawable.like_button)
        } else {
            track.isLiked = true
            views.likeButton.setImageResource(R.drawable.like_button_active)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> finish()

            R.id.play_button -> viewModel.playbackControl()

            R.id.like_button -> {
                viewModel.toggleLike(track)
                toggleLikeView()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onActivityDestroyed()
    }
}