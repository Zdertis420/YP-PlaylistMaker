package orc.zdertis420.playlistmaker.ui.adapter.track

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackAdapter(private var tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    private var onItemClickListener: ((position: Int) -> Unit)? = null

    private var onItemLongClickListener: ((position: Int) -> Boolean)? = null

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemHoldListener(listener: (position: Int) -> Boolean) {
        onItemLongClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.track,
                    parent,
                    false
                ),
            onItemClickListener,
            onItemLongClickListener
        )
    }

    @SuppressLint("NotifyDataSetChanged") // ya know what?? fuck you and your warnings
    fun updateTracks(newTracks: List<Track>) {
        this.tracks = newTracks
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
}