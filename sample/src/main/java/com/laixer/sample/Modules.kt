package com.laixer.sample

import com.laixer.cache.ReactiveCache
import com.laixer.network.createNetworkClient
import com.laixer.sample.data.datasource.UserCacheDataSource
import com.laixer.sample.data.datasource.UserRemoteDataSource
import com.laixer.sample.data.datasource.VlogCacheDataSource
import com.laixer.sample.data.datasource.VlogRemoteDataSource
import com.laixer.sample.data.datasource.ReactionCacheDataSource
import com.laixer.sample.data.datasource.ReactionRemoteDataSource
import com.laixer.sample.data.repository.ReactionRepositoryImpl
import com.laixer.sample.data.repository.VlogRepositoryImpl
import com.laixer.sample.data.repository.UserRepositoryImpl
import com.laixer.sample.datasource.cache.ReactionCacheDataSourceImpl
import com.laixer.sample.datasource.cache.VlogCacheDataSourceImpl
import com.laixer.sample.datasource.cache.UserCacheDataSourceImpl
import com.laixer.sample.datasource.model.UserEntity
import com.laixer.sample.datasource.model.VlogEntity
import com.laixer.sample.datasource.remote.ReactionRemoteDataSourceImpl
import com.laixer.sample.datasource.remote.ReactionsApi
import com.laixer.sample.datasource.remote.UserRemoteDataSourceImpl
import com.laixer.sample.datasource.remote.UsersApi
import com.laixer.sample.datasource.remote.VlogRemoteDataSourceImpl
import com.laixer.sample.datasource.remote.VlogsApi
import com.laixer.sample.domain.model.Reaction
import com.laixer.sample.domain.repository.ReactionRepository
import com.laixer.sample.domain.repository.VlogRepository
import com.laixer.sample.domain.repository.UserRepository
import com.laixer.sample.domain.usecase.ReactionsUseCase
import com.laixer.sample.domain.usecase.UserReactionUseCase
import com.laixer.sample.domain.usecase.UserVlogUseCase
import com.laixer.sample.domain.usecase.UsersVlogsUseCase
import com.laixer.sample.presentation.vlogdetails.VlogDetailsViewModel
import com.laixer.sample.presentation.vloglist.VlogListViewModel
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
        networkModule,
        cacheModule
    )
}

val viewModelModule: Module = module {
    viewModel { VlogListViewModel(usersVlogsUseCase = get()) }
    viewModel { VlogDetailsViewModel(userVlogUseCase = get(), reactionsUseCase = get()) }
}

val useCaseModule: Module = module {
    factory { UsersVlogsUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserVlogUseCase(userRepository = get(), vlogRepository = get()) }
    factory { UserReactionUseCase(userRepository = get(), reactionRepository = get()) }
    factory { ReactionsUseCase(reactionRepository = get()) }
}

val repositoryModule: Module = module {
    single { UserRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as UserRepository }
    single { VlogRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as VlogRepository }
    single { ReactionRepositoryImpl(cacheDataSource = get(), remoteDataSource = get()) as ReactionRepository }
}

val dataSourceModule: Module = module {
    single { UserCacheDataSourceImpl(cache = get(USER_CACHE)) as UserCacheDataSource }
    single { UserRemoteDataSourceImpl(api = usersApi) as UserRemoteDataSource }
    single { VlogCacheDataSourceImpl(cache = get(VLOG_CACHE)) as VlogCacheDataSource }
    single { VlogRemoteDataSourceImpl(api = vlogsApi) as VlogRemoteDataSource }
    single { ReactionCacheDataSourceImpl(cache = get(REACTION_CACHE)) as ReactionCacheDataSource }
    single { ReactionRemoteDataSourceImpl(api = reactionsApi) as ReactionRemoteDataSource }
}

val networkModule: Module = module {
    single { usersApi }
    single { vlogsApi }
    single { reactionsApi }
}

val cacheModule: Module = module {
    single(name = USER_CACHE) { ReactiveCache<List<UserEntity>>() }
    single(name = VLOG_CACHE) { ReactiveCache<List<VlogEntity>>() }
    single(name = REACTION_CACHE) { ReactiveCache<List<Reaction>>() }
}

private const val BASE_URL = "https://my-json-server.typicode.com/pnobbe/swabbrdata/"

private val retrofit: Retrofit = createNetworkClient(BASE_URL, BuildConfig.DEBUG)

private val vlogsApi: VlogsApi = retrofit.create(VlogsApi::class.java)
private val usersApi: UsersApi = retrofit.create(UsersApi::class.java)
private val reactionsApi: ReactionsApi = retrofit.create(ReactionsApi::class.java)

private const val USER_CACHE = "USER_CACHE"
private const val VLOG_CACHE = "VLOG_CACHE"
private const val REACTION_CACHE = "REACTION_CACHE"
