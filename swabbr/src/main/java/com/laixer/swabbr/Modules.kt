package com.laixer.swabbr

import com.laixer.cache.ReactiveCache
import com.laixer.network.createNetworkClient
import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.data.repository.FollowRepositoryImpl
import com.laixer.swabbr.data.repository.ReactionRepositoryImpl
import com.laixer.swabbr.data.repository.SettingsRepositoryImpl
import com.laixer.swabbr.data.repository.UserRepositoryImpl
import com.laixer.swabbr.data.repository.VlogRepositoryImpl
import com.laixer.swabbr.datasource.cache.ReactionCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.SettingsCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.UserCacheDataSourceImpl
import com.laixer.swabbr.datasource.cache.VlogCacheDataSourceImpl
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.VlogEntity
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
import com.laixer.swabbr.domain.repository.FollowRepository
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.SettingsRepository
import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.ReactionsUseCase
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UserVlogUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.presentation.profile.ProfileViewModel
import com.laixer.swabbr.presentation.search.SearchViewModel
import com.laixer.swabbr.presentation.settings.SettingsViewModel
import com.laixer.swabbr.presentation.vlogdetails.VlogDetailsViewModel
import com.laixer.swabbr.presentation.vloglist.VlogListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

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
    viewModel { ProfileViewModel(usersUseCase = get(), userVlogsUseCase = get(), followUseCase = get()) }
    viewModel { VlogListViewModel(usersVlogsUseCase = get()) }
    viewModel { VlogDetailsViewModel(usersVlogsUseCase = get(), reactionsUseCase = get()) }
    viewModel { SearchViewModel(usersUseCase = get()) }
    viewModel { SettingsViewModel(settingsUseCase = get()) }
}
val useCaseModule: Module = module {
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
    single { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as UserRepository }
    single { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as VlogRepository }
    single { ReactionRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as ReactionRepository }
    single { FollowRepositoryImpl(dataSource = get()) as FollowRepository }
    single { SettingsRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as SettingsRepository }
}
val dataSourceModule: Module = module {
    single { UserCacheDataSourceImpl(cache = get(named(USER_CACHE))) as UserCacheDataSource }
    single { UserRemoteDataSourceImpl(api = usersApi) as UserRemoteDataSource }
    single { VlogCacheDataSourceImpl(cache = get(named(VLOG_CACHE))) as VlogCacheDataSource }
    single { VlogRemoteDataSourceImpl(api = vlogsApi) as VlogRemoteDataSource }
    single { ReactionCacheDataSourceImpl(cache = get(named(REACTION_CACHE))) as ReactionCacheDataSource }
    single { ReactionRemoteDataSourceImpl(api = reactionsApi) as ReactionRemoteDataSource }
    single { FollowDataSourceImpl(api = followApi) as FollowDataSource }
    single { SettingsCacheDataSourceImpl(cache = get(named(SETTINGS_CACHE))) as SettingsCacheDataSource }
    single { SettingsRemoteDataSourceImpl(api = settingsApi) as SettingsRemoteDataSource }
}
val networkModule: Module = module {
    single { usersApi }
    single { vlogsApi }
    single { reactionsApi }
    single { followApi }
    single { settingsApi }
}
val cacheModule: Module = module {
    single(named(USER_CACHE)) { ReactiveCache<List<UserEntity>>() }
    single(named(VLOG_CACHE)) { ReactiveCache<List<VlogEntity>>() }
    single(named(REACTION_CACHE)) { ReactiveCache<List<Reaction>>() }
    single(named(SETTINGS_CACHE)) { ReactiveCache<Settings>() }
}
private const val BASE_URL = "https://my-json-server.typicode.com/pnobbe/swabbrdata/"
private val retrofit: Retrofit = createNetworkClient(BASE_URL, BuildConfig.DEBUG)
private val vlogsApi: VlogsApi = retrofit.create(VlogsApi::class.java)
private val usersApi: UsersApi = retrofit.create(UsersApi::class.java)
private val reactionsApi: ReactionsApi = retrofit.create(ReactionsApi::class.java)
private val settingsApi: SettingsApi = retrofit.create(SettingsApi::class.java)
private val followApi: FollowApi = retrofit.create(FollowApi::class.java)
private const val USER_CACHE = "USER_CACHE"
private const val VLOG_CACHE = "VLOG_CACHE"
private const val REACTION_CACHE = "REACTION_CACHE"
private const val SETTINGS_CACHE = "SETTINGS_CACHE"
