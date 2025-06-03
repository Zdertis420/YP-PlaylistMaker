package orc.zdertis420.playlistmaker.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.FragmentPlaylistsBinding
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _views: FragmentPlaylistsBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            views.noPlaylistsImg.setImageResource(R.drawable.empty_result_dark)
        } else {
            views.noPlaylistsImg.setImageResource(R.drawable.empty_result_light)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}
