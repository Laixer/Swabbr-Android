package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single

interface VlogRepository {

    fun getUserVlogs(userId: String, refresh: Boolean): Single<List<Vlog>>

    fun get(vlogId: String, refresh: Boolean): Single<Vlog>

    fun getFeaturedVlogs(): Single<List<Vlog>>
}
