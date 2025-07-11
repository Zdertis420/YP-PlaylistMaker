package orc.zdertis420.playlistmaker.ui.adapter.playlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import orc.zdertis420.playlistmaker.databinding.PlaylistLibraryBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist

class LibraryPlaylistAdapter(private var playlists: List<Playlist>) :
    RecyclerView.Adapter<LibraryPlaylistViewHolder>() {
    private var onItemClickListener: ((position: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryPlaylistViewHolder {
        return LibraryPlaylistViewHolder(
            views = PlaylistLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClickListener = onItemClickListener
        )
    }

    override fun onBindViewHolder(
        holder: LibraryPlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(playlists: List<Playlist>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }
}