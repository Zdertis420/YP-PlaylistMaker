package orc.zdertis420.playlistmaker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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


}