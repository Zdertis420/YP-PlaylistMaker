package orc.zdertis420.playlistmaker.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var views: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)

        views.search.setOnClickListener(this)
        views.mediaLibrary.setOnClickListener(this)
        views.settings.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.media_library -> startActivity(Intent(this, MediaLibraryActivity::class.java))
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}