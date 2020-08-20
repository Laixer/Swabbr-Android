package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.data.datasource.model.WatchVlogResponse
import com.laixer.swabbr.domain.model.LikeList
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface VlogCacheDataSource {

    val key: String
        get() = "VLOGS"

    val recommendedKey: String
        get() = "RECOMMENDED_VLOGS"

    fun getUserVlogs(userId: UUID): Single<List<Vlog>>

    fun set(list: List<Vlog>): Single<List<Vlog>>

    fun get(vlogId: UUID): Single<Vlog>

    fun set(item: Vlog): Single<Vlog>

    fun getRecommendedVlogs(): Single<List<Vlog>>

    fun setRecommendedVlogs(list: List<Vlog>): Single<List<Vlog>>
}

interface VlogRemoteDataSource {

    fun getUserVlogs(userId: UUID): Single<List<Vlog>>

    fun get(vlogId: UUID): Single<Vlog>

    fun getRecommendedVlogs(): Single<List<Vlog>>

    fun getLikes(vlogId: UUID): Single<LikeList>

    fun like(vlogId: UUID): Completable

    fun unlike(vlogId: UUID): Completable

    fun watch(vlogId: UUID): Single<WatchVlogResponse>
}
