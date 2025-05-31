package orc.zdertis420.playlistmaker.ui.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.databinding.FragmentLikedBinding
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.activity.PlayerActivity
import orc.zdertis420.playlistmaker.ui.adapter.TrackAdapter
import orc.zdertis420.playlistmaker.ui.viewmodel.LikedViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.LikedState
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedFragment : Fragment() {

    companion object {
        fun newInstance(): LikedFragment {
            val fragment = LikedFragment()
            return fragment
        }
    }

    private var _views: FragmentLikedBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<LikedViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentLikedBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.likedTracks.adapter = TrackAdapter(emptyList())

        (views.likedTracks.adapter as TrackAdapter).setOnItemClickListener { position ->
            val track = (viewModel.screenState.value as LikedState.LikedTracks).tracks[position]
            startPlayerActivity(track)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect { state ->
                    render(state)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.observeLikedTracks()
    }

    private fun startPlayerActivity(track: Track) {
        val startPlayerActivity = Intent(requireActivity(), PlayerActivity::class.java)

        startPlayerActivity.putExtra("track", track.toDto())

        startActivity(startPlayerActivity)
    }

    private fun render(state: LikedState) {
        when (state) {
            LikedState.Empty -> showEmpty()
            is LikedState.Error -> showError(state.msg)
            is LikedState.LikedTracks -> showLiked(state.tracks)
            LikedState.Loading -> showLoading()
        }
    }

    private fun showEmpty() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            views.noTracksLikedImg.setImageResource(R.drawable.empty_result_dark)
        } else {
            views.noTracksLikedImg.setImageResource(R.drawable.empty_result_light)
        }

        views.likedTracks.visibility = View.GONE
        views.progressBar.visibility = View.GONE
        views.noTracksLiked.visibility = View.VISIBLE
    }

    private fun showError(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showLiked(tracks: List<Track>) {
        (views.likedTracks.adapter as TrackAdapter).updateTracks(tracks)

        views.noTracksLiked.visibility = View.GONE
        views.progressBar.visibility = View.GONE
        views.likedTracks.visibility = View.VISIBLE
    }

    private fun showLoading() {
        views.noTracksLiked.visibility = View.GONE
        views.likedTracks.visibility = View.GONE
        views.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}