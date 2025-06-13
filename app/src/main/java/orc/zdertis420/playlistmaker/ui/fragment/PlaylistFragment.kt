package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
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

    private lateinit var tracksBottomSheet: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistMenuBottomSheet: BottomSheetBehavior<LinearLayout>

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.shareStateFlow.collect { intent ->
                    if (intent != null) {
                        startActivity(intent)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.playlistStateFlow.collect { playlist ->
                    if (playlist != null) {
                        this@PlaylistFragment.playlist = playlist
                        setupFields()
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener("edit_result", viewLifecycleOwner) { requestKey, bundle ->
            val wasPlaylistChanged = bundle.getBoolean("was_edited", true)

            if (wasPlaylistChanged) {
                viewModel.updateUI()
            }
        }
    }

    private fun setupListeners() {
        views.back.setOnClickListener {
            handleBackPressed()
        }

        views.share.setOnClickListener {
            Log.d("PLAYLIST", "Sharing playlist")
            if (playlist.tracks.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.no_tracks), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sharePlaylist()
            }
        }

        views.more.setOnClickListener {
            Log.d("PLAYLIST", "Playlist menu opened")
            playlistMenuBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        views.sharePlaylist.setOnClickListener {
            if (playlist.tracks.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.no_tracks), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sharePlaylist()
            }
        }

        views.editPlaylist.setOnClickListener {
            val args = bundleOf("playlist" to playlist.toDto())
            findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, args)
        }

        views.deletePlaylist.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.alertDialogStyle)
                .setTitle("${getString(R.string.delete_playlist_question)} ${playlist.name}?")
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    viewModel.deletePlaylist()
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                        View.VISIBLE
                    requireActivity().findViewById<View>(R.id.delimiter).visibility = View.VISIBLE
                    findNavController().navigateUp()
                    dialog.dismiss()
                }
                .show()
        }

        (views.tracks.adapter as TrackAdapter).setOnItemClickListener { position ->
            val track = playlist.tracks[position]
            val args = bundleOf("track" to track.toDto())
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment, args)
        }

        (views.tracks.adapter as TrackAdapter).setOnItemHoldListener { position ->
            MaterialAlertDialogBuilder(requireContext(), R.style.alertDialogStyle)
                .setTitle(getString(R.string.delete_track))
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
        tracksBottomSheet = BottomSheetBehavior.from(views.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        playlistMenuBottomSheet = BottomSheetBehavior.from(views.playlistMenuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        views.tracks.adapter = TrackAdapter(playlist.tracks)
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
            View.GONE
    }

    private fun handleBackPressed() {
        if (playlistMenuBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            playlistMenuBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (playlistMenuBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
            playlistMenuBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (tracksBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            tracksBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
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

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}