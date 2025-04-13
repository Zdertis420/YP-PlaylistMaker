package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.FragmentMediaLibraryBinding
import orc.zdertis420.playlistmaker.ui.adapter.PagerAdapter

class MediaLibraryFragment : Fragment() {

    private var _views: FragmentMediaLibraryBinding? = null
    private val views get() = _views!!

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentMediaLibraryBinding.inflate(inflater, container, false)

        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.mediaViewPager.adapter = PagerAdapter(childFragmentManager, lifecycle)
        setupTabLayout()
    }

    private fun setupTabLayout() {
        tabLayoutMediator = TabLayoutMediator(views.mediaTabLayout, views.mediaViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.liked_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }.also { it.attach() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        _views = null
    }
}