package orc.zdertis420.playlistmaker

import android.content.Context
import orc.zdertis420.playlistmaker.data.network.RetrofitNetworkClient
import orc.zdertis420.playlistmaker.data.repository.PlayerRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.ThemeRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackHistoryRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.api.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.api.PlayerRepository
import orc.zdertis420.playlistmaker.domain.api.ThemeInteractor
import orc.zdertis420.playlistmaker.domain.api.ThemeRepository
import orc.zdertis420.playlistmaker.domain.api.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.api.TrackHistoryRepository
import orc.zdertis420.playlistmaker.domain.api.TrackInteractor
import orc.zdertis420.playlistmaker.domain.api.TrackRepository
import orc.zdertis420.playlistmaker.domain.implementations.PlayerInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.ThemeInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.TrackHistoryInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.TrackInteractorImplementation

object Creator {
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImplementation(getTrackRepository())
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImplementation(RetrofitNetworkClient())
    }

    fun provideTrackHistoryInteractor(context: Context): TrackHistoryInteractor {
        return TrackHistoryInteractorImplementation(getTrackHistoryRepository(context))
    }

    private fun getTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImplementation(context)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImplementation(getPlayerRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImplementation()
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImplementation(getThemeRepository(context))
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImplementation(context)
    }

}