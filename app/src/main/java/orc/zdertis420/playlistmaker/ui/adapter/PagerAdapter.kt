package orc.zdertis420.playlistmaker.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import orc.zdertis420.playlistmaker.ui.fragment.FragmentLiked
import orc.zdertis420.playlistmaker.ui.fragment.FragmentPlaylists

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentLiked()
            1 -> FragmentPlaylists()
            else -> throw IllegalArgumentException()
        }
    }
}