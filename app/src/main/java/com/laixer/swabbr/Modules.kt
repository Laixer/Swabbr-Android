package com.laixer.swabbr

import android.accounts.AbstractAccountAuthenticator
import android.accounts.AccountManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.laixer.cache.Cache
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.data.datasource.FollowCacheDataSource
import com.laixer.swabbr.data.datasource.FollowRemoteDataSource
import com.laixer.swabbr.data.datasource.LivestreamDataSource
import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.data.datasource.cache.AuthCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.cache.FollowCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.cache.ReactionCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.cache.SettingsCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.cache.UserCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.cache.VlogCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.AuthRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.FollowRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.LivestreamDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.ReactionRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.SettingsRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.UserRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.VlogRemoteDataSourceImpl
import com.laixer.swabbr.data.repository.AuthRepositoryImpl
import com.laixer.swabbr.data.repository.FollowRepositoryImpl
import com.laixer.swabbr.data.repository.LivestreamRepositoryImpl
import com.laixer.swabbr.data.repository.ReactionRepositoryImpl
import com.laixer.swabbr.data.repository.SettingsRepositoryImpl
import com.laixer.swabbr.data.repository.UserRepositoryImpl
import com.laixer.swabbr.data.repository.VlogRepositoryImpl
import com.laixer.swabbr.data.datasource.model.remote.AuthApi
import com.laixer.swabbr.data.datasource.model.remote.FollowApi
import com.laixer.swabbr.data.datasource.model.remote.LivestreamApi
import com.laixer.swabbr.data.datasource.model.remote.ReactionsApi
import com.laixer.swabbr.data.datasource.model.remote.SettingsApi
import com.laixer.swabbr.data.datasource.model.remote.UsersApi
import com.laixer.swabbr.data.datasource.model.remote.VlogsApi
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.FollowRepository
import com.laixer.swabbr.domain.repository.LivestreamRepository
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.SettingsRepository
import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.LivestreamUseCase
import com.laixer.swabbr.domain.usecase.ReactionsUseCase
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UserVlogUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.domain.usecase.VlogsUseCase
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.auth.AuthenticatorService
import com.laixer.swabbr.presentation.auth.SimpleAuthenticator
import com.laixer.swabbr.presentation.auth.UserManager
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.livestream.LivestreamViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.profile.settings.SettingsViewModel
import com.laixer.swabbr.presentation.vlogs.details.VlogDetailsViewModel
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = BuildConfig.API_ENDPOINT

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            authModule,
            firebaseModule,
            viewModelModule,
            useCaseModule,
            repositoryModule,
            dataSourceModule,
            networkModule,
            cacheModule
        )
    )
}

val firebaseModule: Module = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseAnalytics.getInstance(androidContext())}
}

val authModule: Module = module {
    factory<AccountManager> { AccountManager.get(androidContext()) }
    single<AbstractAccountAuthenticator> { SimpleAuthenticator(androidContext(), get(), get()) }
    single { UserManager(get(), get()) }
}

val viewModelModule: Module = module {
    viewModel { LivestreamViewModel(livestreamUseCase = get()) }
    viewModel { AuthViewModel(userManager = get(), authUseCase = get()) }
    viewModel { ProfileViewModel(usersUseCase = get(), userVlogsUseCase = get(), followUseCase = get()) }
    viewModel { VlogListViewModel(usersVlogsUseCase = get(), vlogsUseCase = get()) }
    viewModel {
        VlogDetailsViewModel(
            userVlogsUseCase = get(),
            userVlogUseCase = get(),
            reactionsUseCase = get(),
            vlogsUseCase = get()
        )
    }
    viewModel { SearchViewModel(usersUseCase = get()) }
    viewModel { SettingsViewModel(settingsUseCase = get()) }
}
val useCaseModule: Module = module {
    factory { LivestreamUseCase(livestreamRepository = get()) }
    factory { AuthUseCase(authRepository = get()) }
    factory { UsersUseCase(userRepository = get()) }
    factory { UsersVlogsUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogsUseCase(vlogRepository = get(), userRepository = get()) }
    factory { VlogsUseCase(vlogRepository = get()) }
    factory { UserReactionUseCase(userRepository = get(), reactionRepository = get()) }
    factory { ReactionsUseCase(reactionRepository = get()) }
    factory { FollowUseCase(followRepository = get()) }
    factory { SettingsUseCase(repository = get()) }
}
val repositoryModule: Module = module {
    single<AuthRepository> {
        AuthRepositoryImpl(
            authCacheDataSource = get(),
            authRemoteDataSource = get()
        )
    }
    single<LivestreamRepository> { LivestreamRepositoryImpl(livestreamDataSource = get()) }
    single<UserRepository> { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<VlogRepository> { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<ReactionRepository> { ReactionRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<FollowRepository> { FollowRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
}
val dataSourceModule: Module = module {
    single<AuthCacheDataSource> { AuthCacheDataSourceImpl(get()) }
    single<LivestreamDataSource> { LivestreamDataSourceImpl(livestreamApi = get()) }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(authApi = get(), settingsApi = get()) }
    single<UserCacheDataSource> { UserCacheDataSourceImpl(cache = get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(api = get()) }
    single<VlogCacheDataSource> { VlogCacheDataSourceImpl(cache = get()) }
    single<VlogRemoteDataSource> { VlogRemoteDataSourceImpl(api = get()) }
    single<ReactionCacheDataSource> { ReactionCacheDataSourceImpl(cache = get()) }
    single<ReactionRemoteDataSource> { ReactionRemoteDataSourceImpl(api = get()) }
    single<FollowRemoteDataSource> { FollowRemoteDataSourceImpl(api = get()) }
    single<FollowCacheDataSource> { FollowCacheDataSourceImpl(cache = get()) }
    single<SettingsCacheDataSource> { SettingsCacheDataSourceImpl(cache = get()) }
    single<SettingsRemoteDataSource> { SettingsRemoteDataSourceImpl(api = get()) }
}
val networkModule: Module = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single { AuthInterceptor(authCacheDataSource = get()) }

    single<LivestreamApi> { get<Retrofit>().create(LivestreamApi::class.java) }
    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
    single<UsersApi> { get<Retrofit>().create(UsersApi::class.java) }
    single<VlogsApi> { get<Retrofit>().create(VlogsApi::class.java) }
    single<ReactionsApi> { get<Retrofit>().create(ReactionsApi::class.java) }
    single<FollowApi> { get<Retrofit>().create(FollowApi::class.java) }
    single<SettingsApi> { get<Retrofit>().create(SettingsApi::class.java) }
}
val cacheModule: Module = module {
    single { Cache() }
}
