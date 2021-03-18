package com.laixer.swabbr

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.cache.Cache
import com.laixer.swabbr.data.api.*
import com.laixer.swabbr.data.cache.*
import com.laixer.swabbr.data.datasource.*
import com.laixer.swabbr.data.interfaces.*
import com.laixer.swabbr.data.repository.*
import com.laixer.swabbr.domain.interfaces.*
import com.laixer.swabbr.domain.usecase.*
import com.laixer.swabbr.extensions.buildWithCustomAdapters
import com.laixer.swabbr.services.okhttp.AuthInterceptor
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.services.users.UserManager
import com.laixer.swabbr.presentation.likeoverview.LikeOverviewViewModel
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.reaction.list.ReactionListViewModel
import com.laixer.swabbr.presentation.reaction.playback.ReactionViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import com.laixer.swabbr.presentation.vlogs.playback.VlogViewModel
import com.laixer.swabbr.presentation.vlogs.recording.VlogRecordingViewModel
import com.laixer.swabbr.services.okhttp.CacheInterceptor
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
    single { UserManager(get()) } // This must be a singleton!
}

val viewModelModule: Module = module {
    viewModel { LikeOverviewViewModel(vlogLikeOverviewUseCase = get(), followUseCase = get()) }
    viewModel {
        AuthViewModel(
            userManager = get(),
            authUseCase = get(),
            firebaseMessaging = get()
        )
    }
    viewModel {
        ProfileViewModel(
            usersUseCase = get(),
            vlogUseCase = get(),
            followUseCase = get(),
            authUserUseCase = get()
        )
    }
    viewModel { VlogListViewModel(usersVlogsUseCase = get(), vlogUseCase = get()) }
    viewModel { VlogViewModel(authUserUseCase = get(), reactionsUseCase = get(), vlogUseCase = get()) }
    viewModel { VlogRecordingViewModel(mHttpClient = get(), vlogUseCase = get(), context = androidContext()) }
    viewModel { SearchViewModel(usersUseCase = get(), followUseCase = get()) }
    viewModel { ReactionViewModel(mHttpClient = get(), reactionsUseCase = get(), context = androidContext()) }
    viewModel { ReactionListViewModel(reactionsUseCase = get()) }
}

val useCaseModule: Module = module {
    factory { AuthUserUseCase(userRepository = get(), userManager = get()) }
    factory { AuthUseCase(authRepository = get()) }
    factory { UsersUseCase(userRepository = get()) }
    factory {
        VlogUseCase(
            userRepository = get(),
            vlogRepository = get(),
            vlogLikeRepository = get(),
            reactionRepository = get()
        )
    }
    factory { VlogLikeOverviewUseCase(vlogLikeRepository = get(), userRepository = get()) }
    factory { ReactionUseCase(userRepository = get(), reactionRepository = get()) }
    factory { FollowUseCase(followRequestRepository = get(), userRepository = get()) }
}

val repositoryModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<UserRepository> { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<VlogRepository> { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<VlogLikeRepository> { VlogLikeRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
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
    single<VlogLikeCacheDataSource> { VlogLikeCacheDataSourceImpl(cache = get()) }
    single<VlogLikeDataSource> { VlogLikeDataSourceImpl(api = get()) }
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
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().buildWithCustomAdapters()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    // TODO Should this be a single?
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<CacheInterceptor>())
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BASIC // TODO Put back
            })
            .cache(okhttp3.Cache(File(androidContext().cacheDir, "http-cache"), 10 * 1024 * 1024)) // 10Mb cache TODO Do we want this?
            .connectTimeout(5, TimeUnit.SECONDS) // TODO Fix for production
            .readTimeout(300, TimeUnit.SECONDS)
            .callTimeout(300, TimeUnit.SECONDS)
            .build()
    }

    single { CacheInterceptor() }
    single { AuthInterceptor(userManager = get()) }

    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
    single<UserApi> { get<Retrofit>().create(UserApi::class.java) }
    single<VlogApi> { get<Retrofit>().create(VlogApi::class.java) }
    single<VlogLikeApi> { get<Retrofit>().create(VlogLikeApi::class.java) }
    single<ReactionApi> { get<Retrofit>().create(ReactionApi::class.java) }
    single<FollowRequestApi> { get<Retrofit>().create(FollowRequestApi::class.java) }
}

val cacheModule: Module = module {
    single { Cache() }
}
