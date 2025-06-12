package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.dto.PlaylistDto
import orc.zdertis420.playlistmaker.data.mapper.toDto
import orc.zdertis420.playlistmaker.data.mapper.toPlaylist
import orc.zdertis420.playlistmaker.databinding.FragmentPlaylistBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.ui.adapter.track.TrackAdapter
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class PlaylistFragment : Fragment() {

    private var _views: FragmentPlaylistBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var playlist: Playlist

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            views.overlay.visibility = when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> View.GONE
                else -> View.VISIBLE
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentPlaylistBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigation()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )

        playlist = requireArguments().getParcelable<PlaylistDto>("playlist")!!.toPlaylist()

        viewModel.setPlaylist(playlist)

        setupBottomSheets()
        setupFields()
        setupListeners()
    }

    private fun setupListeners() {
        views.back.setOnClickListener {
            handleBackPressed()
        }



        (views.tracks.adapter as TrackAdapter).setOnItemClickListener { position ->
            val track = playlist.tracks[position]
            val args = bundleOf("track" to track.toDto())
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment, args)
        }

        (views.tracks.adapter as TrackAdapter).setOnItemHoldListener { position ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delte_track))
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    val track = playlist.tracks[position]
                    viewModel.deleteTrackFromPlaylist(track)
                    playlist = playlist.copy(tracks = playlist.tracks.filter { it.trackId != track.trackId })
                    setupFields()
                    (views.tracks.adapter as TrackAdapter).updateTracks(playlist.tracks)
                    dialog.dismiss()
                }
                .show()

            return@setOnItemHoldListener true
        }
    }

    private fun setupBottomSheets() {
        bottomSheetBehavior = BottomSheetBehavior.from(views.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
            addBottomSheetCallback(bottomSheetCallback)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(views.playlistMenuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = true
            addBottomSheetCallback(bottomSheetCallback)
        }

        views.tracks.adapter = TrackAdapter(playlist.tracks)
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
            View.GONE
    }

    private fun handleBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                View.VISIBLE
            requireActivity().findViewById<View>(R.id.delimiter).visibility = View.VISIBLE
            findNavController().popBackStack()
        }
    }

    fun setupFields() {
        with(views) {
            playlistName.text = playlist.name
            year.text = playlist.year.toString()

            val durationMinutes = playlist.tracks.sumOf { it.trackTimeMillis }
                .let { millis ->
                    TimeUnit.MILLISECONDS.toMinutes(millis).toString()
                }.toInt()

            duration.text = views.root.resources.getQuantityString(
                R.plurals.minutes_plurals,
                durationMinutes,
                durationMinutes
            )

            trackAmount.text = views.root.resources.getQuantityString(
                R.plurals.tracks_plurals,
                playlist.tracks.size,
                playlist.tracks.size
            )

            Glide.with(this@PlaylistFragment)
                .load(playlist.imagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(playlistImage)

            Glide.with(this@PlaylistFragment)
                .load(playlist.imagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(playlistMini.playlistImage)

            playlistMini.playlistName.text = playlist.name
            playlistMini.playlistCount.text = views.root.resources.getQuantityString(
                R.plurals.tracks_plurals,
                playlist.tracks.size,
                playlist.tracks.size
            )
        }
    }
}