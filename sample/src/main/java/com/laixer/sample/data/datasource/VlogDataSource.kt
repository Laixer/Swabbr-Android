package com.laixer.sample.data.datasource

import com.laixer.sample.domain.model.Vlog
import io.reactivex.Single

interface VlogCacheDataSource {

    fun get(): Single<List<Vlog>>

    fun set(list: List<Vlog>): Single<List<Vlog>>

    fun get(vlogId: String): Single<Vlog>

    fun set(item: Vlog): Single<Vlog>
}

interface VlogRemoteDataSource {

    fun get(): Single<List<Vlog>>

    fun get(vlogId: String): Single<Vlog>
}
