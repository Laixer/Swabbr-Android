package com.laixer.swabbr

import com.laixer.cache.MemoryCache
import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.*
import com.laixer.swabbr.data.datasource.cache.*
import com.laixer.swabbr.data.datasource.remote.*
import com.laixer.swabbr.data.repository.AuthRepositoryImpl
import com.laixer.swabbr.data.repository.FollowRepositoryImpl
import com.laixer.swabbr.data.repository.ReactionRepositoryImpl
import com.laixer.swabbr.data.repository.SettingsRepositoryImpl
import com.laixer.swabbr.data.repository.UserRepositoryImpl
import com.laixer.swabbr.data.repository.VlogRepositoryImpl
import com.laixer.swabbr.datasource.model.remote.AuthApi
import com.laixer.swabbr.datasource.model.remote.FollowApi
import com.laixer.swabbr.datasource.model.remote.ReactionsApi
import com.laixer.swabbr.datasource.model.remote.SettingsApi
import com.laixer.swabbr.datasource.model.remote.UsersApi
import com.laixer.swabbr.datasource.model.remote.VlogsApi
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.FollowRepository
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.SettingsRepository
import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.ReactionsUseCase
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UserVlogUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.domain.usecase.VlogsUseCase
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.settings.SettingsViewModel
import com.laixer.swabbr.presentation.vlogdetails.VlogDetailsViewModel
import com.laixer.swabbr.presentation.vloglist.VlogListViewModel
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://swabbr-backend-development-android.azurewebsites.net/"

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            viewModelModule,
            useCaseModule,
            repositoryModule,
            dataSourceModule,
            networkModule,
            cacheModule
        )
    )
}
val viewModelModule: Module = module {
    viewModel { AuthViewModel(authUseCase = get()) }
    viewModel { ProfileViewModel(usersUseCase = get(), userVlogsUseCase = get(), followUseCase = get()) }
    viewModel { VlogListViewModel(usersVlogsUseCase = get(), vlogsUseCase = get()) }
    viewModel { VlogDetailsViewModel(usersVlogsUseCase = get(), reactionsUseCase = get()) }
    viewModel { SearchViewModel(usersUseCase = get()) }
    viewModel { SettingsViewModel(settingsUseCase = get()) }
}
val useCaseModule: Module = module {
    factory { AuthUseCase(authRepository = get()) }
    factory { UsersUseCase(userRepository = get()) }
    factory { UsersVlogsUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogsUseCase(vlogRepository = get()) }
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
    single<UserRepository> { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<VlogRepository> { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<ReactionRepository> { ReactionRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<FollowRepository> { FollowRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) }
}
val dataSourceModule: Module = module {
    single<AuthCacheDataSource> {
        AuthCacheDataSourceImpl(
            cache = get(named(AUTH_CACHE)),
            memory = get(named(AUTH_MEMORY))
        )
    }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(authApi = get(), settingsApi = get()) }
    single<UserCacheDataSource> { UserCacheDataSourceImpl(cache = get(named(USER_CACHE))) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(api = get()) }
    single<VlogCacheDataSource> { VlogCacheDataSourceImpl(cache = get(named(VLOG_CACHE))) }
    single<VlogRemoteDataSource> { VlogRemoteDataSourceImpl(api = get()) }
    single<ReactionCacheDataSource> { ReactionCacheDataSourceImpl(cache = get(named(REACTION_CACHE))) }
    single<ReactionRemoteDataSource> { ReactionRemoteDataSourceImpl(api = get()) }
    single<FollowRemoteDataSource> { FollowRemoteDataSourceImpl(api = get()) }
    single<FollowCacheDataSource> { FollowCacheDataSourceImpl(cache = get(named(FOLLOW_CACHE))) }
    single<SettingsCacheDataSource> { SettingsCacheDataSourceImpl(cache = get(named(SETTINGS_CACHE))) }
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

    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
    single<UsersApi> { get<Retrofit>().create(UsersApi::class.java) }
    single<VlogsApi> { get<Retrofit>().create(VlogsApi::class.java) }
    single<ReactionsApi> { get<Retrofit>().create(ReactionsApi::class.java) }
    single<FollowApi> { get<Retrofit>().create(FollowApi::class.java) }
    single<SettingsApi> { get<Retrofit>().create(SettingsApi::class.java) }
}
val cacheModule: Module = module {
    single(named(AUTH_MEMORY)) { MemoryCache<AuthUser>() }
    single(named(AUTH_CACHE)) { ReactiveCache<AuthUser>() }
    single(named(USER_CACHE)) { ReactiveCache<User>() }
    single(named(VLOG_CACHE)) { ReactiveCache<List<Vlog>>() }
    single(named(REACTION_CACHE)) { ReactiveCache<List<Reaction>>() }
    single(named(SETTINGS_CACHE)) { ReactiveCache<Settings>() }
    single(named(FOLLOW_CACHE)) { ReactiveCache<List<User>>() }
}
const val AUTH_MEMORY = "AUTH_MEMORY"
const val AUTH_CACHE = "AUTH_CACHE"
const val FOLLOW_CACHE = "FOLLOW_CACHE"
const val USER_CACHE = "USER_CACHE"
const val VLOG_CACHE = "VLOG_CACHE"
const val REACTION_CACHE = "REACTION_CACHE"
const val SETTINGS_CACHE = "SETTINGS_CACHE"
