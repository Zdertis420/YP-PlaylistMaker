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
import orc.zdertis420.playlistmaker.ui.viewmodel.LikedViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.PlayerViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistsViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.SettingsViewModel
import orc.zdertis420.playlistmaker.utils.KeyboardUtil
import orc.zdertis420.playlistmaker.utils.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //Player
    factory<PlayerInteractor> { PlayerInteractorImplementation(get<PlayerRepository>()) }
    single<PlayerRepository> { PlayerRepositoryImplementation(get<MediaPlayer>()) }
    single { MediaPlayer() }
    //SavedState

    //Player VM
    viewModel<PlayerViewModel> { PlayerViewModel(get<PlayerInteractor>(), get()) }

    //Search
    //Tracks browsing
    factory<TrackInteractor> { TrackInteractorImplementation(get<TrackRepository>()) }
    single<TrackRepository> { TrackRepositoryImplementation(get<RetrofitNetworkClient>()) }
    factory { RetrofitNetworkClient() }
    //History
    factory<TrackHistoryInteractor> { TrackHistoryInteractorImplementation(get<TrackHistoryRepository>()) }
    single<TrackHistoryRepository> { TrackHistoryRepositoryImplementation(androidContext()) }
    //NetworkUtil
    single<NetworkUtil> { NetworkUtil(androidContext()) }
    //Search VM
    viewModel<SearchViewModel> {
        SearchViewModel(
            get<TrackInteractor>(),
            get<TrackHistoryInteractor>(),
            get<NetworkUtil>()
        )
    }
    //KeyboardUtil
    single<KeyboardUtil> { KeyboardUtil(androidContext()) }

    //Settings
    //Theme
    factory<ThemeInteractor> { ThemeInteractorImplementation(get<ThemeRepository>()) }
    single<ThemeRepository> { ThemeRepositoryImplementation(androidContext()) }
    //ShareApp
    factory<ShareAppUseCase> { ShareAppUseCaseImplementation(get<ShareAppRepository>()) }
    single<ShareAppRepository> { ShareAppRepositoryImplementation(androidContext()) }
    //SeeEula
    factory<SeeEulaUseCase> { SeeEulaUseCaseImplementation(get<SeeEulaRepository>()) }
    single<SeeEulaRepository> { SeeEulaRepositoryImplementation(androidContext()) }
    //ContactSupport
    factory<ContactSupportUseCase> { ContactSupportUseCaseImplementation(get<ContactSupportRepository>()) }
    single<ContactSupportRepository> { ContactSupportRepositoryImplementation(androidContext()) }
    //Settings VM
    viewModel<SettingsViewModel> {
        SettingsViewModel(
            get<ThemeInteractor>(),
            get<ShareAppUseCase>(),
            get<ContactSupportUseCase>(),
            get<SeeEulaUseCase>()
        )
    }

    //Liked
    viewModel<LikedViewModel> { LikedViewModel() }

    //Playlists
    viewModel<PlaylistsViewModel> { PlaylistsViewModel() }
}