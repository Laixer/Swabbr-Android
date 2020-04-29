package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single
import java.util.UUID

interface VlogRepository {

    fun getUserVlogs(userId: UUID, refresh: Boolean): Single<List<Vlog>>

    fun get(vlogId: UUID, refresh: Boolean): Single<Vlog>

    fun getRecommendedVlogs(refresh: Boolean): Single<List<Vlog>>
}
