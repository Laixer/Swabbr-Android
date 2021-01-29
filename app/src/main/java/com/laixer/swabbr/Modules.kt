package com.laixer.swabbr

import android.accounts.AbstractAccountAuthenticator
import android.accounts.AccountManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
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
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.profile.settings.SettingsViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.reaction.ReactionViewModel
import com.laixer.swabbr.presentation.vlogs.details.VlogDetailsViewModel
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import com.laixer.swabbr.presentation.vlogs.recording.VlogRecordingViewModel
import com.laixer.swabbr.utils.BuildWithCustomAdapters
import com.squareup.moshi.Moshi
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
    single { FirebaseMessaging.getInstance() }
}

val authModule: Module = module {
    factory<AccountManager> { AccountManager.get(androidContext()) }
    single<AbstractAccountAuthenticator> { SimpleAuthenticator(androidContext(), get(), get()) }
    single { UserManager(get(), get()) }
}

val viewModelModule: Module = module {
    viewModel { MainActivityViewModel(userManager = get()) }
    viewModel { AuthUserViewModel(userManager = get(), authUserUseCase = get(), followUseCase = get()) }
    viewModel { AuthViewModel(userManager = get(), authUserUseCase = get(), authUseCase = get(), firebaseMessaging = get()) }
    viewModel { ProfileViewModel(usersUseCase = get(), vlogUseCase = get(), followUseCase = get(), authUserUseCase = get()) }
    viewModel { VlogListViewModel(usersVlogsUseCase = get(), vlogUseCase = get()) }
    viewModel { VlogDetailsViewModel(reactionsUseCase = get(), vlogUseCase = get()) }
    viewModel { VlogRecordingViewModel(mHttpClient = get(), vlogUseCase = get(), context = androidContext()) }
    viewModel { SearchViewModel(usersUseCase = get()) }
    viewModel { SettingsViewModel(settingsUseCase = get()) }
    viewModel { ReactionViewModel(mHttpClient = get(), reactionsUseCase = get(), context = androidContext()) }
}
val useCaseModule: Module = module {
    factory { AuthUserUseCase(userRepository = get(), followRequestRepository = get()) }
    factory { AuthUseCase(authRepository = get()) }
    factory { UsersUseCase(userRepository = get()) }
    factory { VlogUseCase(userRepository = get(), vlogRepository = get(), reactionRepository = get()) }
    factory { ReactionUseCase(userRepository = get(), reactionRepository = get()) }
    factory { ReactionsUseCase(reactionRepository = get()) }
    factory { FollowUseCase(followRequestRepository = get(), userRepository = get()) }
    factory { SettingsUseCase(userRepository = get()) }
}
val repositoryModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<UserRepository> { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<VlogRepository> { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<ReactionRepository> { ReactionRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<FollowRequestRepository> { FollowRequestRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
}
val dataSourceModule: Module = module {
    single<AuthCacheDataSource> { AuthCacheDataSourceImpl(get()) }
    single<AuthDataSource> { AuthRemoteDataSourceImpl(authApi = get()) }
    single<UserCacheDataSource> { UserCacheDataSourceImpl(cache = get()) }
    single<UserDataSource> { UserDataSourceImpl(api = get()) }
    single<VlogCacheDataSource> { VlogCacheDataSourceImpl(cache = get()) }
    single<VlogDataSource> { VlogDataSourceImpl(api = get()) }
    single<ReactionCacheDataSource> { ReactionCacheDataSourceImpl(cache = get()) }
    single<ReactionDataSource> { ReactionDataSourceImpl(api = get()) }
    single<FollowRequestDataSource> { FollowRequestRemoteDataSourceImpl(api = get()) }
    single<FollowRequestCacheDataSource> { FollowRequestCacheDataSourceImpl(cache = get()) }
}
val networkModule: Module = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().BuildWithCustomAdapters()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<com.laixer.swabbr.CacheInterceptor>())
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            })
            .cache(okhttp3.Cache(File(androidContext().cacheDir, "http-cache"), 10 * 1024 * 1024)) // 10Mb cache
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single { com.laixer.swabbr.CacheInterceptor() }
    single { AuthInterceptor(userManager = get()) }

    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
    single<UserApi> { get<Retrofit>().create(UserApi::class.java) }
    single<VlogApi> { get<Retrofit>().create(VlogApi::class.java) }
    single<ReactionApi> { get<Retrofit>().create(ReactionApi::class.java) }
    single<FollowRequestApi> { get<Retrofit>().create(FollowRequestApi::class.java) }
}
val cacheModule: Module = module {
    single { Cache() }
}
