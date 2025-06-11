package orc.zdertis420.playlistmaker.ui.adapter.playlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import orc.zdertis420.playlistmaker.databinding.PlaylistPlayerBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist

class PlayerPlaylistAdapter(private var playlists: List<Playlist>) :
    RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    private var onItemClickListener: ((position: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        return PlayerPlaylistViewHolder(
            views = PlaylistPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClickListener = onItemClickListener
        )
    }

    override fun onBindViewHolder(
        holder: PlayerPlaylistViewHolder,
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