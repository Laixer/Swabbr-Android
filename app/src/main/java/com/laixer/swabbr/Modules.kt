package com.laixer.swabbr

import android.accounts.AbstractAccountAuthenticator
import android.accounts.AccountManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.*
import com.laixer.swabbr.data.datasource.cache.*
import com.laixer.swabbr.data.datasource.model.remote.*
import com.laixer.swabbr.data.datasource.remote.*
import com.laixer.swabbr.data.repository.*
import com.laixer.swabbr.domain.repository.*
import com.laixer.swabbr.domain.usecase.*
import com.laixer.swabbr.presentation.MainActivityViewModel
import com.laixer.swabbr.presentation.auth.AuthUserViewModel
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.auth.SimpleAuthenticator
import com.laixer.swabbr.presentation.auth.UserManager
import com.laixer.swabbr.presentation.livestream.LivestreamViewModel
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.profile.settings.SettingsViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.vlogs.details.VlogDetailsViewModel
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import io.reactivex.schedulers.Schedulers
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.internal.cache.CacheInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

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
    single { FirebaseAnalytics.getInstance(androidContext()) }
}

val authModule: Module = module {
    factory<AccountManager> { AccountManager.get(androidContext()) }
    single<AbstractAccountAuthenticator> { SimpleAuthenticator(androidContext(), get(), get()) }
    single { UserManager(get(), get()) }
}

val viewModelModule: Module = module {
    viewModel { MainActivityViewModel(userManager = get()) }
    viewModel { AuthUserViewModel(userManager = get(), authUserUseCase = get()) }
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
    factory { AuthUserUseCase(authRepository = get(), userRepository = get()) }
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
    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            authApi = get(),
            settingsApi = get(),
            usersApi = get(),
            followApi = get()
        )
    }
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
            .addInterceptor(get<com.laixer.swabbr.CacheInterceptor>())
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            })
            .cache(okhttp3.Cache(File(androidContext().cacheDir, "http-cache"), 10 * 1024 * 1024)) // 10Mb cache
            .callTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    single { com.laixer.swabbr.CacheInterceptor() }
    single { AuthInterceptor(userManager = get()) }

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
