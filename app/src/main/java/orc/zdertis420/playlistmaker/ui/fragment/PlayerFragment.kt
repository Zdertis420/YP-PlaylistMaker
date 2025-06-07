package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.dto.TrackDto
import orc.zdertis420.playlistmaker.data.mapper.toTrack
import orc.zdertis420.playlistmaker.databinding.FragmentPlayerBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.ui.adapter.playlist.PlayerPlaylistAdapter
import orc.zdertis420.playlistmaker.ui.viewmodel.PlayerViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlayerState
import orc.zdertis420.playlistmaker.ui.viewmodel.states.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment(), View.OnClickListener {

    private var _views: FragmentPlayerBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<PlayerViewModel>()

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

    private lateinit var track: Track
    private var previewUrl = ""

    private val simpleDate by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentPlayerBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigation()
        setupBottomSheet()
        setupListeners()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )

        track = requireArguments().getParcelable<TrackDto>("track")!!.toTrack()

        views.playButton.isEnabled = false

        views.timePlaying.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)

        viewModel.setTrack(track)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerStateFlow.collect { state ->
                    render(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.likeStateFlow.collect { isLiked ->
                    toggleLikeView(isLiked)
                    track.isLiked = isLiked
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistStateFlow.collect {
                    when (it) {
                        PlaylistsState.Empty -> {}
                        PlaylistsState.Error -> Toast.makeText(
                            requireActivity(), getString(R.string.loading_error),
                            Toast.LENGTH_SHORT
                        ).show()

                        is PlaylistsState.Playlists -> showPlaylists(it.playlists)
                    }
                }
            }
        }

        loadTrack(track)

        Log.d("THREAD", Thread.currentThread().toString())

        viewModel.observeLiked()

        viewModel.loadPlaylists()

        viewModel.prepare()
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
            View.GONE
        requireActivity().findViewById<View>(R.id.delimiter).visibility = View.GONE
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(views.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            addBottomSheetCallback(bottomSheetCallback)
        }

        views.playlists.adapter = PlayerPlaylistAdapter(emptyList())
        (views.playlists.adapter as PlayerPlaylistAdapter).setOnItemClickListener { position ->
            val playlist =
                (viewModel.playlistStateFlow.value as PlaylistsState.Playlists).playlists[position]

            if (playlist.tracks.contains(track)) {
                Toast.makeText(
                    requireActivity(), getString(R.string.track_is_in_playlist) + playlist.name,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.addToPlaylist(playlist.id, track)
                Toast.makeText(
                    requireActivity(), getString(R.string.track_added_to_playlist) + playlist.name,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        (views.playlists.adapter as PlayerPlaylistAdapter).updatePlaylists(playlists)
    }

    private fun setupListeners() {
        views.back.setOnClickListener(this)
        views.playButton.setOnClickListener(this)
        views.likeButton.setOnClickListener(this)
        views.saveToLibrary.setOnClickListener(this)
        views.newPlaylist.setOnClickListener(this)
        views.overlay.setOnClickListener(this)
    }

    private fun render(state: PlayerState) {
        Log.d("STATE", state.toString())

        when (state) {
            is PlayerState.Prepared -> showPrepared()
            is PlayerState.Play -> showPlaying(state.remainingMillis)
            is PlayerState.Pause -> showPause()
            is PlayerState.Error -> {
                Log.e("ERROR", state.msg)
                Toast.makeText(requireActivity(), state.msg, Toast.LENGTH_SHORT).show()
            }

            PlayerState.Preparing -> Toast.makeText(
                requireActivity(),
                R.string.preparing,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showPrepared() {
        views.playButton.isEnabled = true
        views.timePlaying.text = "00:00"
    }

    private fun showPlaying(remainingMillis: Long) {
        views.playButton.setImageResource(R.drawable.pause_button)

        views.timePlaying.text = simpleDate.format(remainingMillis)
    }

    private fun showPause() {
        views.playButton.setImageResource(R.drawable.play_button)
    }

    private fun loadTrack(track: Track) {

        Log.d("current", track.toString())

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .transform(RoundedCorners(8))
            .timeout(2500)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(views.trackImage)


        views.trackName.text = track.trackName
        views.trackAuthor.text = track.artistName
        views.country.text = track.country
        views.genre.text = track.primaryGenreName
        views.year.text = track.releaseDate.substring(0, 4)
        views.album.text = track.collectionName
        views.duration.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)
        previewUrl = track.previewUrl



        views.trackName.isSelected = true
        views.trackAuthor.isSelected = true
        views.country.isSelected = true
        views.genre.isSelected = true
        views.album.isSelected = true
    }

    private fun toggleLikeView(isLiked: Boolean) {
        if (isLiked) {
            views.likeButton.setImageResource(R.drawable.like_button_active)
        } else {
            views.likeButton.setImageResource(R.drawable.like_button)
        }
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> handleBackPressed()

            R.id.play_button -> viewModel.playbackControl()

            R.id.like_button -> {
                Log.d("TRACK", "Like toggled for ${track.trackName}: ${!track.isLiked}. Activity")

                viewModel.toggleLike(track)
                viewModel.observeLiked()
            }

            R.id.save_to_library -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            R.id.new_playlist -> findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)

            R.id.overlay -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onActivityDestroyed()
    }
}