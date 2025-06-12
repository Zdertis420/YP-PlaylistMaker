package orc.zdertis420.playlistmaker.di

import android.media.MediaPlayer
import androidx.room.Room
import orc.zdertis420.playlistmaker.data.db.DataBase
import orc.zdertis420.playlistmaker.data.network.RetrofitNetworkClient
import orc.zdertis420.playlistmaker.data.repository.ContactSupportRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.CreatePlaylistRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.PlayerRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.PlaylistRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.SaveImageRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.SeeEulaRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.ShareAppRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.ThemeRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackHistoryRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackLikedRepositoryImplementation
import orc.zdertis420.playlistmaker.data.repository.TrackSearchRepositoryImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.PlayerInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.PlaylistInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.ThemeInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackHistoryInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.interactor.TrackLikedInteractorImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.ContactSupportUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.CreatePlaylistUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.SaveImageUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.SeeEulaUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.implementations.usecase.ShareAppUseCaseImplementation
import orc.zdertis420.playlistmaker.domain.interactor.PlayerInteractor
import orc.zdertis420.playlistmaker.domain.interactor.PlaylistInteractor
import orc.zdertis420.playlistmaker.domain.interactor.ThemeInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackHistoryInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackInteractor
import orc.zdertis420.playlistmaker.domain.interactor.TrackLikedInteractor
import orc.zdertis420.playlistmaker.domain.repository.ContactSupportRepository
import orc.zdertis420.playlistmaker.domain.repository.CreatePlaylistRepository
import orc.zdertis420.playlistmaker.domain.repository.PlayerRepository
import orc.zdertis420.playlistmaker.domain.repository.PlaylistRepository
import orc.zdertis420.playlistmaker.domain.repository.SaveImageRepository
import orc.zdertis420.playlistmaker.domain.repository.SeeEulaRepository
import orc.zdertis420.playlistmaker.domain.repository.ShareAppRepository
import orc.zdertis420.playlistmaker.domain.repository.ThemeRepository
import orc.zdertis420.playlistmaker.domain.repository.TrackHistoryRepository
import orc.zdertis420.playlistmaker.domain.repository.TrackLikedRepository
import orc.zdertis420.playlistmaker.domain.repository.TrackSearchRepository
import orc.zdertis420.playlistmaker.domain.usecase.ContactSupportUseCase
import orc.zdertis420.playlistmaker.domain.usecase.CreatePlaylistUseCase
import orc.zdertis420.playlistmaker.domain.usecase.SaveImageUseCase
import orc.zdertis420.playlistmaker.domain.usecase.SeeEulaUseCase
import orc.zdertis420.playlistmaker.domain.usecase.ShareAppUseCase
import orc.zdertis420.playlistmaker.ui.viewmodel.CreatePlaylistViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.LikedViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.PlayerViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.PlaylistsViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.SearchViewModel
import orc.zdertis420.playlistmaker.ui.viewmodel.SettingsViewModel
import orc.zdertis420.playlistmaker.utils.KeyboardUtil
import orc.zdertis420.playlistmaker.utils.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val player = module {
    //Dependencies
    factory<TrackLikedInteractor> { TrackLikedInteractorImplementation(get<TrackLikedRepository>()) }
    single<TrackLikedRepository> { TrackLikedRepositoryImplementation(get()) }
    factory<PlayerInteractor> { PlayerInteractorImplementation(get<PlayerRepository>()) }
    single<PlayerRepository> { PlayerRepositoryImplementation(get<MediaPlayer>()) }
    single { MediaPlayer() }

    single<PlaylistRepository> { PlaylistRepositoryImplementation(get()) }
    factory<PlaylistInteractor> { PlaylistInteractorImplementation(get<PlaylistRepository>()) }

    //Player VM
    viewModel<PlayerViewModel> {
        PlayerViewModel(
            get<PlayerInteractor>(),
            get(),
            get<TrackLikedInteractor>(),
            get<PlaylistInteractor>()
        )
    }
}

val search = module {
    //Tracks browsing
    factory<TrackInteractor> { TrackInteractorImplementation(get<TrackSearchRepository>()) }
    single<TrackSearchRepository> { TrackSearchRepositoryImplementation(get<RetrofitNetworkClient>()) }
    factory { RetrofitNetworkClient() }
    //History
    factory<TrackHistoryInteractor> { TrackHistoryInteractorImplementation(get<TrackHistoryRepository>()) }
    single<TrackHistoryRepository> { TrackHistoryRepositoryImplementation(androidContext()) }
    //NetworkUtil
    single<NetworkUtil> { NetworkUtil(androidContext()) }
    //KeyboardUtil
    single<KeyboardUtil> { KeyboardUtil(androidContext()) }

    //Search VM
    viewModel<SearchViewModel> {
        SearchViewModel(
            get<TrackInteractor>(),
            get<TrackHistoryInteractor>(),
            get<NetworkUtil>()
        )
    }
}

val settings = module {
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
}

val liked = module {
    single<TrackLikedRepository> { TrackLikedRepositoryImplementation(get()) }
    factory<TrackLikedInteractor> { TrackLikedInteractorImplementation(get<TrackLikedRepository>()) }
    viewModel<LikedViewModel> { LikedViewModel(get<TrackLikedInteractor>()) }
}

val playlists = module {
    // Creation
    single<CreatePlaylistRepository> { CreatePlaylistRepositoryImplementation(get()) }
    single<SaveImageRepository> { SaveImageRepositoryImplementation(androidContext()) }
    factory<CreatePlaylistUseCase> { CreatePlaylistUseCaseImplementation(get<CreatePlaylistRepository>()) }
    factory<SaveImageUseCase> { SaveImageUseCaseImplementation(get<SaveImageRepository>()) }
    viewModel<CreatePlaylistViewModel> {
        CreatePlaylistViewModel(
            get<CreatePlaylistUseCase>(),
            get<SaveImageUseCase>()
        )
    }

    // Operating
    single<PlaylistRepository> { PlaylistRepositoryImplementation(get()) }
    factory<PlaylistInteractor> { PlaylistInteractorImplementation(get<PlaylistRepository>()) }
    viewModel<PlaylistsViewModel> { PlaylistsViewModel(get<PlaylistInteractor>()) }

    // Ah idk
    viewModel<PlaylistViewModel> { PlaylistViewModel(get<PlaylistInteractor>()) }
}

val database = module {
    single {
        Room.databaseBuilder(androidContext(), DataBase::class.java, "database.db")
            .build()
    }
}