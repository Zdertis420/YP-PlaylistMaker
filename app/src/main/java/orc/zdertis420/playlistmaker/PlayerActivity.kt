package orc.zdertis420.playlistmaker

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity(), View.OnClickListener {

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

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            intent.getParcelableExtra("track") as? Track
        }

        Log.d("TRACK", track.toString())

        if (track != null) {
            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
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

            trackName.isSelected = true
            trackAuthor.isSelected = true
            country.isSelected = true
            genre.isSelected = true
            album.isSelected = true
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> finish()
        }
    }
}