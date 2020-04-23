package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single
import java.util.UUID

interface VlogCacheDataSource {

    val key: String
        get() = "VLOGS"

    val featuredKey: String
        get() = "FEATURED_VLOGS"

    fun getUserVlogs(userId: UUID): Single<List<Vlog>>

    fun set(list: List<Vlog>): Single<List<Vlog>>

    fun get(vlogId: UUID): Single<Vlog>

    fun set(item: Vlog): Single<Vlog>

    fun getFeaturedVlogs(): Single<List<Vlog>>

    fun setFeaturedVlogs(list: List<Vlog>): Single<List<Vlog>>
}

interface VlogRemoteDataSource {

    fun getUserVlogs(userId: UUID): Single<List<Vlog>>

    fun get(vlogId: UUID): Single<Vlog>

    fun getRecommendedVlogs(): Single<List<Vlog>>
}
