package com.laixer.swabbr

import com.laixer.cache.ReactiveCache
import com.laixer.network.createNetworkClient
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.data.repository.AuthRepositoryImpl
import com.laixer.swabbr.data.repository.FollowRepositoryImpl
import com.laixer.swabbr.data.repository.ReactionRepositoryImpl
import com.laixer.swabbr.data.repository.SettingsRepositoryImpl
import com.laixer.swabbr.data.repository.VlogRepositoryImpl
import com.laixer.swabbr.data.repository.UserRepositoryImpl
import com.laixer.swabbr.datasource.cache.AuthCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.ReactionCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.SettingsCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.VlogCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.UserCacheDataSourceImpl
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.VlogEntity
import com.laixer.swabbr.datasource.remote.AuthApi
import com.laixer.swabbr.datasource.remote.AuthRemoteDataSourceImpl
import com.laixer.swabbr.datasource.remote.FollowApi
import com.laixer.swabbr.datasource.remote.FollowDataSourceImpl
import com.laixer.swabbr.datasource.remote.ReactionRemoteDataSourceImpl
import com.laixer.swabbr.datasource.remote.ReactionsApi
import com.laixer.swabbr.datasource.remote.SettingsApi
import com.laixer.swabbr.datasource.remote.SettingsRemoteDataSourceImpl
import com.laixer.swabbr.datasource.remote.UserRemoteDataSourceImpl
import com.laixer.swabbr.datasource.remote.UsersApi
import com.laixer.swabbr.datasource.remote.VlogRemoteDataSourceImpl
import com.laixer.swabbr.datasource.remote.VlogsApi
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.FollowRepository
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.SettingsRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.ReactionsUseCase
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UserVlogUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.presentation.login.LoginViewModel
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.registration.RegistrationViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.settings.SettingsViewModel
import com.laixer.swabbr.presentation.vlogdetails.VlogDetailsViewModel
import com.laixer.swabbr.presentation.vloglist.VlogListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataSourceModule,
        cacheModule,
        networkModule
        )
}

val viewModelModule: Module = module {
    viewModel { LoginViewModel(authUseCase = get()) }
    viewModel { RegistrationViewModel(authUseCase = get()) }
    viewModel { ProfileViewModel(usersUseCase = get(), userVlogsUseCase = get(), followUseCase = get()) }
    viewModel { VlogListViewModel(usersVlogsUseCase = get()) }
    viewModel { VlogDetailsViewModel(usersVlogsUseCase = get(), reactionsUseCase = get()) }
    viewModel { SearchViewModel(usersUseCase = get()) }
    viewModel { SettingsViewModel(settingsUseCase = get(), authUseCase = get()) }
}

val useCaseModule: Module = module {
    factory { AuthUseCase(authRepository = get(), userRepository = get(), settingsRepository = get()) }
    factory { UsersUseCase(userRepository = get()) }
    factory { UsersVlogsUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogsUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserReactionUseCase(userRepository = get(), reactionRepository = get()) }
    factory { ReactionsUseCase(reactionRepository = get()) }
    factory { FollowUseCase(followRepository = get()) }
    factory { SettingsUseCase(settingsRepository = get()) }
}

val repositoryModule: Module = module {
    single { AuthRepositoryImpl(authCacheDataSource = get(), authRemoteDataSource = get()) as AuthRepository }
    single { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as UserRepository }
    single { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as VlogRepository }
    single { ReactionRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as ReactionRepository }
    single { FollowRepositoryImpl(dataSource = get()) as FollowRepository }
    single { SettingsRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as SettingsRepository }
}

val dataSourceModule: Module = module {
    single { AuthCacheDataSourceImpl(cache = get(AUTH_CACHE)) as AuthCacheDataSource}
    single { AuthRemoteDataSourceImpl(api = get(AUTH_API)) as AuthRemoteDataSource }
    single { UserCacheDataSourceImpl(cache = get(USER_CACHE)) as UserCacheDataSource }
    single { UserRemoteDataSourceImpl(api = get(USER_API)) as UserRemoteDataSource }
    single { VlogCacheDataSourceImpl(cache = get(VLOG_CACHE)) as VlogCacheDataSource }
    single { VlogRemoteDataSourceImpl(api = get(VLOG_API)) as VlogRemoteDataSource }
    single { ReactionCacheDataSourceImpl(cache = get(REACTION_CACHE)) as ReactionCacheDataSource }
    single { ReactionRemoteDataSourceImpl(api = get(REACTION_API)) as ReactionRemoteDataSource }
    single { FollowDataSourceImpl(api = get(FOLLOW_API)) as FollowDataSource }
    single { SettingsCacheDataSourceImpl(cache = get(SETTINGS_CACHE)) as SettingsCacheDataSource }
    single { SettingsRemoteDataSourceImpl(api = get(SETTINGS_API)) as SettingsRemoteDataSource }
}

val networkModule: Module = module {
    single(name = AUTH_INTERCEPTOR) { AuthInterceptor(authCacheDataSource = get()) }
    single(name = RETROFIT) { createNetworkClient(get(AUTH_INTERCEPTOR), BASE_URL, BuildConfig.DEBUG) }
    single(name = AUTH_API) { provideAuthApi(get(RETROFIT)) }
    single(name = USER_API) { provideUsersApi(get(RETROFIT)) }
    single(name = VLOG_API) { provideVlogsApi(get(RETROFIT)) }
    single(name = REACTION_API) { provideReactionsApi(get(RETROFIT)) }
    single(name = FOLLOW_API) { provideFollowApi(get(RETROFIT)) }
    single(name = SETTINGS_API) { provideSettingsApi(get(RETROFIT)) }
}

val cacheModule: Module = module {
    single(name = AUTH_CACHE) { ReactiveCache<Pair<String, String>>() }
    single(name = USER_CACHE) { ReactiveCache<List<UserEntity>>() }
    single(name = VLOG_CACHE) { ReactiveCache<List<VlogEntity>>() }
    single(name = REACTION_CACHE) { ReactiveCache<List<Reaction>>() }
    single(name = SETTINGS_CACHE) { ReactiveCache<Settings>() }
}

// private const val BASE_URL = "https://my-json-server.typicode.com/pnobbe/swabbrdata/"
private const val BASE_URL = "https://swabbr.azurewebsites.net/"

private fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
private fun provideVlogsApi(retrofit: Retrofit): VlogsApi = retrofit.create(VlogsApi::class.java)
private fun provideUsersApi(retrofit: Retrofit): UsersApi = retrofit.create(UsersApi::class.java)
private fun provideReactionsApi(retrofit: Retrofit): ReactionsApi = retrofit.create(ReactionsApi::class.java)
private fun provideSettingsApi(retrofit: Retrofit): SettingsApi = retrofit.create(SettingsApi::class.java)
private fun provideFollowApi(retrofit: Retrofit): FollowApi = retrofit.create(FollowApi::class.java)

private const val RETROFIT = "RETROFIT"
private const val AUTH_API = "AUTH_API"
private const val VLOG_API = "VLOG_API"
private const val USER_API = "USER_API"
private const val REACTION_API = "REACTION_API"
private const val SETTINGS_API = "SETTINGS_API"
private const val FOLLOW_API = "FOLLOW_API"

private const val AUTH_INTERCEPTOR = "AUTH_INTERCEPTOR"

private const val AUTH_CACHE = "AUTH_CACHE"
private const val USER_CACHE = "USER_CACHE"
private const val VLOG_CACHE = "VLOG_CACHE"
private const val REACTION_CACHE = "REACTION_CACHE"
private const val SETTINGS_CACHE = "SETTINGS_CACHE"
