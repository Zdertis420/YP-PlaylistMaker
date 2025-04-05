package orc.zdertis420.playlistmaker.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.FragmentLikedBinding

class FragmentLiked : Fragment() {

    private var _views: FragmentLikedBinding? = null
    private val views get() = _views

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentLikedBinding.inflate(inflater, container, false)
        return views?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            views?.noTracksLiked?.setImageResource(R.drawable.empty_result_dark)
        } else {
            views?.noTracksLiked?.setImageResource(R.drawable.empty_result_light)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}