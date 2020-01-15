package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single

interface VlogCacheDataSource {

    fun get(): Single<List<Vlog>>

    fun set(list: List<Vlog>): Single<List<Vlog>>

    fun get(vlogId: String): Single<Vlog>

    fun set(item: Vlog): Single<Vlog>
}

interface VlogRemoteDataSource {

    fun getUserVlogs(userId: String): Single<List<Vlog>>

    fun get(vlogId: String): Single<Vlog>

    fun getFeaturedVlogs(): Single<List<Vlog>>
}
