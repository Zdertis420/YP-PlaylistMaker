package orc.zdertis420.playlistmaker

import android.content.Context
import orc.zdertis420.playlistmaker.data.network.RetrofitNetworkClient
import orc.zdertis420.playlistmaker.data.repository.PlayerRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.ThemeRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackHistoryRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import orc.zdertis420.playlistmaker.domain.repository.ThemeRepository
import orc.zdertis420.playlistmaker.domain.interactor.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.repository.TrackHistoryRepository
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor
import orc.zdertis420.playlistmaker.domain.repository.TrackRepository
import orc.zdertis420.playlistmaker.domain.implementations.interactor.PlayerInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.ThemeInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackHistoryInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.ContactSupportUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.SeeEulaUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.ShareAppUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.usecase.ContactSupportUseCase
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase
import orc.zdertis420.playlistmaker.domain.usecase.ShareAppUseCase

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

    fun provideShareAppUseCase(context: Context): ShareAppUseCase {
        return ShareAppUseCaseImplementation(context)
    }

    fun provideContactSupportUSeCase(context: Context): ContactSupportUseCase {
        return ContactSupportUseCaseImplementation(context)
    }

    fun provideSeeEulaUseCase(context: Context): SeeEulaUseCase {
        return SeeEulaUseCaseImplementation(context)
    }

}