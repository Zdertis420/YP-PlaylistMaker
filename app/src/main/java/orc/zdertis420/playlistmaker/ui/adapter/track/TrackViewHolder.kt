package orc.zdertis420.playlistmaker.ui.adapter.track

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import orc.zdertis420.playlistmaker.R
import orc.zdertis420.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    itemView: View,
    private val onItemClickListener: ((position: Int) -> Unit)?,
    private val onItemClickLongListener: ((position: Int) -> Boolean)?
) :
    RecyclerView.ViewHolder(itemView) {

    private var trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameView: TextView = itemView.findViewById(R.id.author)
    private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
    private val trackImageView: ShapeableImageView = itemView.findViewById(R.id.track_image)


    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        artistNameView.requestLayout()
        trackTimeView.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(model.trackTimeMillis)
        trackTimeView.requestLayout()
        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .timeout(2500)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(trackImageView)

        trackNameView.isSelected = true
        artistNameView.isSelected = true
    }

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }

        itemView.setOnLongClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickLongListener?.invoke(position) ?: false
            } else {
                false
            }
        }
    }
}