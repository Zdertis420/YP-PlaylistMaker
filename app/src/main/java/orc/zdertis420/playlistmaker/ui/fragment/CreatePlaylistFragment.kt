package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.FragmentCreatePlaylistBinding

class CreatePlaylistFragment : Fragment() {

    private var _views: FragmentCreatePlaylistBinding? = null
    private val views get() = _views!!

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

        views.back.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility = View.VISIBLE
            requireActivity().findViewById<View>(R.id.delimiter).visibility = View.VISIBLE
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility = View.GONE
        requireActivity().findViewById<View>(R.id.delimiter).visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _views = null
    }
}