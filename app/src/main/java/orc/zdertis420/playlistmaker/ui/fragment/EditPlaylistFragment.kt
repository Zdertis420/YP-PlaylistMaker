package orc.zdertis420.playlistmaker.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.data.dto.PlaylistDto
import orc.zdertis420.playlistmaker.data.mapper.toPlaylist
import orc.zdertis420.playlistmaker.databinding.FragmentEditPlaylistBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist
import orc.zdertis420.playlistmaker.ui.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : Fragment() {

    private var _views: FragmentEditPlaylistBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<EditPlaylistViewModel>()

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.d("PLAYLIST", "Image uri: $it")
                viewModel.onImageSelected(it)
                views.playlistImage.setScaleType(ImageView.ScaleType.CENTER_CROP)
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("IMAGE PERMISSION", "Permission req launched")

            if (granted) {
                imagePickerLauncher.launch("image/*")
            }
        }

    private var playlist: Playlist? = null
    private var wasEdited = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigation()

        playlist = requireArguments().getParcelable<PlaylistDto>("playlist")?.toPlaylist()

        if (playlist != null) {
            setupFields()
        }


        views.playlistImage.setOnClickListener {
            Log.d("PLAYLIST", "Check and req permission")

            checkAndRequestPermission()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )

        views.back.setOnClickListener {
            handleBackPressed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistEditingState.collect { result ->
                    result?.let {
                        if (it.isSuccess) {
                            if (wasEdited) {
                                parentFragmentManager.setFragmentResult(
                                    "edit_result",
                                    bundleOf("was_edited" to true)
                                )
                            }

                            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                                View.VISIBLE
                            requireActivity().findViewById<View>(R.id.delimiter).visibility =
                                View.VISIBLE
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                requireActivity().getString(R.string.creation_error) + it.exceptionOrNull()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        views.playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNotEmpty = !s.isNullOrBlank()
                setCreateButtonState(isNotEmpty)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        views.createPlaylist.setOnClickListener {
            val name = views.playlistName.text.toString().trim()
            val description =
                views.playlistDescription.text.toString().trim().takeIf { it.isNotEmpty() }

            if (name.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.name_cant_be_empty), Toast.LENGTH_SHORT).show()
            } else if (playlist == null) {
                viewModel.createPlaylist(name, description.toString())
            } else {
                wasEdited = true
                viewModel.updatePlaylist(playlist!!.id, name, description.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedImageUri.collectLatest { uri ->
                    if (uri != null) {
                        views.playlistImage.setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun setupFields() {
        with(views) {
            Glide.with(requireContext())
                .load(playlist!!.imagePath)
                .apply(RequestOptions().transform(RoundedCorners((8 * resources.displayMetrics.density).toInt())))
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(playlistImage)

            playlistName.setText(playlist!!.name)
            playlistDescription.setText(playlist!!.description)
            createPlaylist.setText(R.string.save)
            setCreateButtonState(true)
        }
        Log.d("PLAYLIST", "URI: ${playlist!!.imagePath}")
        viewModel.onImageSelected(playlist!!.imagePath!!.toUri())
    }

    private fun setCreateButtonState(enabled: Boolean) {
        views.createPlaylist.isEnabled = enabled
        val colorRes = if (enabled) R.color.focused_box_color else R.color.unfocused_box_color
        views.createPlaylist.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                colorRes
            )
        )
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        Log.d("IMAGE PERMISSION", permission)

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("IMAGE PERMISSION", "Permission granted, launching image picker")

                imagePickerLauncher.launch("image/*")
            }

            shouldShowRequestPermissionRationale(permission) -> {
                Log.d("IMAGE PERMISSION", "Permission not granted, launching permission req")

                permissionLauncher.launch(permission)
            }

            else -> {
                Log.d("IMAGE PERMISSION", "wtf, launching permission req")

                permissionLauncher.launch(permission)
            }
        }
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
            View.GONE
        requireActivity().findViewById<View>(R.id.delimiter).visibility = View.GONE
    }

    private fun handleBackPressed() {
        if (playlist != null) {
            Log.d("PLAYLIST", "Just go fucking back")
            findNavController().popBackStack()
        } else {
            val isImageEmpty = viewModel.selectedImageUri.value == null
            val isNameEmpty = views.playlistName.text.toString().isBlank()
            val isDescriptionEmpty = views.playlistDescription.text.toString().isBlank()

            if (isImageEmpty && isNameEmpty && isDescriptionEmpty) {
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                    View.VISIBLE
                requireActivity().findViewById<View>(R.id.delimiter).visibility = View.VISIBLE
                findNavController().popBackStack()
            } else {
                MaterialAlertDialogBuilder(requireContext(), R.style.alertDialogStyle)
                    .setTitle(requireActivity().getString(R.string.confirm_exit))
                    .setMessage(requireActivity().getString(R.string.playlist_data_warning))
                    .setNegativeButton(requireActivity().getString(R.string.cancel)) { dialog, _ -> }
                    .setPositiveButton(requireActivity().getString(R.string.confirm)) { _, _ ->
                        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                            View.VISIBLE
                        requireActivity().findViewById<View>(R.id.delimiter).visibility =
                            View.VISIBLE
                        findNavController().popBackStack()
                    }
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}