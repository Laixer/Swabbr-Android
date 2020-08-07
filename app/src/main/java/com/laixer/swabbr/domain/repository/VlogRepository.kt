package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Like
import com.laixer.swabbr.domain.model.LikeList
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface VlogRepository {

    fun getUserVlogs(userId: UUID, refresh: Boolean): Single<List<Vlog>>

    fun get(vlogId: UUID, refresh: Boolean = false): Single<Vlog>

    fun getRecommendedVlogs(refresh: Boolean = false): Single<List<Vlog>>

    fun getLikes(vlogId: UUID): Single<LikeList>

    fun like(vlogId: UUID): Completable

    fun unlike(vlogId: UUID): Completable
}
