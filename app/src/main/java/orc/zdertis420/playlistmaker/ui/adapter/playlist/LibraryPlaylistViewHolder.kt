package orc.zdertis420.playlistmaker.ui.adapter.playlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.databinding.PlaylistLibraryBinding
import orc.zdertis420.playlistmaker.domain.entities.Playlist

class LibraryPlaylistViewHolder(
    private val views: PlaylistLibraryBinding,
    private val onItemClickListener: ((position: Int) -> Unit)?
) : RecyclerView.ViewHolder(views.root) {

    init {
        views.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }

    fun bind(model: Playlist) = with(views) {
        playlistName.text = model.name
        playlistCount.text = views.root.resources.getQuantityString(
            R.plurals.tracks_plurals,
            model.tracks.size,
            model.tracks.size
        )

        Glide.with(root)
            .load(model.imagePath)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(2))
            .into(playlistImage)
    }
}