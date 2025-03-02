package orc.zdertis420.playlistmaker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import orc.zdertis420.playlistmaker.domain.api.TrackHistoryRepository
import orc.zdertis420.playlistmaker.domain.entities.Track

class TrackHistoryRepositoryImplementation(private val context: Context) : TrackHistoryRepository {

    private val history by lazy {
        context.getSharedPreferences("HISTORY", Context.MODE_PRIVATE)
    }

    override fun getTrackHistory(): MutableList<Track> {
        return Gson().fromJson(
            history.getString("HISTORY", ""),
            object : TypeToken<MutableList<Track>>() {}.type
        ) ?: mutableListOf()
    }

    override fun saveTrackHistory(trackHistory: MutableList<Track>) {
        history.edit()
            .putString("HISTORY", Gson().toJson(trackHistory).toString())
            .apply()
    }
}