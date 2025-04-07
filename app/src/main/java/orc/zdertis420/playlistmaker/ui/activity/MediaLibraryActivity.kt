package orc.zdertis420.playlistmaker.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.ActivityMediaLibraryBinding
import orc.zdertis420.playlistmaker.ui.adapter.PagerAdapter

class MediaLibraryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var views: ActivityMediaLibraryBinding

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        views = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(views.root)
        ViewCompat.setOnApplyWindowInsetsListener(views.activityMediaLibrary) { view, windowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        views.mediaToolbar.setOnClickListener(this)

        views.mediaViewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)

        tabLayoutMediator = TabLayoutMediator(views.mediaTabLayout, views.mediaViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.liked_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }

        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.media_toolbar -> finish()
        }
    }
}