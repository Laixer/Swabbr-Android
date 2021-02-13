package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 *  Interface for vlog like related API calls.
 */
interface VlogLikeApi {
    @GET("vlog-like/exists/{vlogId}/{userId}")
    fun existsVlogLike(
        @Path("vlogId") vlogId: UUID,
        @Path("userId") userId: UUID
    ): Single<Boolean>

    @GET("vlog-like/{vlogId}/{userId}")
    fun getVlogLike(
        @Path("vlogId") vlogId: UUID,
        @Path("vlogId") userId: UUID
    ): Single<VlogLikeEntity>

    @GET("vlog-like/for-vlog/{vlogId}")
    fun getVlogLikes(
        @Path("vlogId") vlogId: UUID,
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<VlogLikeEntity>>

    @GET("vlog-like/summary/{vlogId}")
    fun getVlogLikeSummary(@Path("vlogId") vlogId: UUID): Single<VlogLikeSummaryEntity>

    @POST("vlog-like/like/{vlogId}")
    fun like(@Path("vlogId") vlogId: UUID): Completable

    @POST("vlog-like/unlike/{vlogId}")
    fun unlike(@Path("vlogId") vlogId: UUID): Completable
}
