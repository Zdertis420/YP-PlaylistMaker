package orc.zdertis420.playlistmaker.di

import orc.zdertis420.playlistmaker.data.repository.PlayerRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.PlayerInteractorImplementation
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository
import android.media.MediaPlayer
import orc.zdertis420.playlistmaker.data.network.RetrofitNetworkClient
import orc.zdertis420.playlistmaker.data.repository.ContactSupportRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.SeeEulaRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.ShareAppRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.ThemeRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackHistoryRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.entities.Track
import orc.zdertis420.playlistmaker.domain.implementations.interactor.ThemeInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackHistoryInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.ContactSupportUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.SeeEulaUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.ShareAppUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor
import orc.zdertis420.playlistmaker.domain.repository.ContactSupportRepository
import orc.zdertis420.playlistmaker.domain.repository.SeeEulaRepository
import orc.zdertis420.playlistmaker.domain.repository.ShareAppRepository
import orc.zdertis420.playlistmaker.domain.repository.ThemeRepository
import orc.zdertis420.playlistmaker.domain.repository.TrackHistoryRepository
import orc.zdertis420.playlistmaker.domain.repository.TrackRepository
import orc.zdertis420.playlistmaker.domain.usecase.ContactSupportUseCase
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase
import orc.zdertis420.playlistmaker.domain.usecase.ShareAppUseCase
import orc.zdertis420.playlistmaker.ui.viewmodel.PlayerViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.SettingsViewModel
import orc.zdertis420.playlistmaker.utils.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //Player
    factory <PlayerInteractor> { PlayerInteractorImplementation(get()) }
    single<PlayerRepository> { PlayerRepositoryImplementation(get<MediaPlayer>()) }
    factory { MediaPlayer() }
    //Player VM
    viewModel<PlayerViewModel> { (track: Track) -> PlayerViewModel(get(), track) }

    //Search
    factory<TrackInteractor> { TrackInteractorImplementation(get()) }
    single<TrackRepository> { TrackRepositoryImplementation(get()) }
    factory { RetrofitNetworkClient() }
    //History
    factory<TrackHistoryInteractor> { TrackHistoryInteractorImplementation(get()) }
    single<TrackHistoryRepository> { TrackHistoryRepositoryImplementation(androidContext()) }
    //NetworkUtil
    single<NetworkUtil> { NetworkUtil(androidContext()) }
    //Search VM
    viewModel<SearchViewModel> { SearchViewModel(get(), get(), get()) }

    //Settings
    //Theme
    factory<ThemeInteractor> { ThemeInteractorImplementation(get()) }
    single<ThemeRepository> { ThemeRepositoryImplementation(androidContext()) }
    //ShareApp
    factory<ShareAppUseCase> { ShareAppUseCaseImplementation(get()) }
    single<ShareAppRepository> { ShareAppRepositoryImplementation(androidContext()) }
    //SeeEula
    factory<SeeEulaUseCase> { SeeEulaUseCaseImplementation(get()) }
    single<SeeEulaRepository> { SeeEulaRepositoryImplementation(androidContext()) }
    //ContactSupport
    factory<ContactSupportUseCase> { ContactSupportUseCaseImplementation(get()) }
    single<ContactSupportRepository> { ContactSupportRepositoryImplementation(androidContext()) }
    //Settings VM
    viewModel<SettingsViewModel> { SettingsViewModel(get(), get(), get(), get()) }
}