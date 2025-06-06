package orc.zdertis420.playlistmaker.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.text.TextWatcher
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.FragmentCreatePlaylistBinding
import orc.zdertis420.playlistmaker.ui.viewmodel.CreatePlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.log

class CreatePlaylistFragment : Fragment() {

    private var _views: FragmentCreatePlaylistBinding? = null
    private val views get() = _views!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onImageSelected(it)
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("IMAGE PERMISSION", "Permission req launched")

            if (granted) {
                imagePickerLauncher.launch("image/*")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _views = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigation()

        views.playlistImage.setOnClickListener {
            Log.d("PLAYLIST", "Chech and req permission")

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistCreationState.collect { result ->
                    result?.let {
                        if (it.isSuccess) {
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

        views.playlistsName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNotEmpty = !s.isNullOrBlank()
                setCreateButtonState(isNotEmpty)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        views.createPlaylist.setOnClickListener {
            val name = views.playlistsName.text.toString().trim()
            val description =
                views.playlistDescription.text.toString().trim().takeIf { it.isNotEmpty() }

            if (name.isNotEmpty()) {
                viewModel.createPlaylist(name, description.toString())
            }

            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                View.VISIBLE
            requireActivity().findViewById<View>(R.id.delimiter).visibility =
                View.VISIBLE

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
        val isImageEmpty = viewModel.selectedImageUri.value == null
        val isNameEmpty = views.playlistsName.text.toString().isBlank()
        val isDescriptionEmpty = views.playlistDescription.text.toString().isBlank()

        if (isImageEmpty && isNameEmpty && isDescriptionEmpty) {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                View.VISIBLE
            requireActivity().findViewById<View>(R.id.delimiter).visibility = View.VISIBLE
            findNavController().popBackStack()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireActivity().getString(R.string.confirm_exit))
                .setMessage(requireActivity().getString(R.string.playlist_data_warning))
                .setNegativeButton(requireActivity().getString(R.string.cancel)) { dialog, _ -> }
                .setPositiveButton(requireActivity().getString(R.string.confirm)) { _, _ ->
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility =
                        View.VISIBLE
                    requireActivity().findViewById<View>(R.id.delimiter).visibility = View.VISIBLE
                    findNavController().popBackStack()
                }
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}