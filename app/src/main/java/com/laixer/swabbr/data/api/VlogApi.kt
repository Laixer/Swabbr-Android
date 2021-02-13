package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 *  Interface for vlog related API calls.
 */
interface VlogApi {
    @POST("vlog/add-views")
    fun addViews(@Body vlogViewsEntity: VlogViewsEntity): Completable

    @DELETE("vlog/{vlogId}")
    fun delete(@Path("vlogId") vlogId: UUID): Completable

    @GET("vlog/{vlogId}")
    fun getVlog(@Path("vlogId") vlogId: UUID): Single<VlogEntity>

    @GET("vlog/generate-upload-uri")
    fun generateUploadWrapper(): Single<UploadWrapperEntity>

    @PUT("vlog/{vlogId}")
    fun updateVlog(
        @Path("vlogId") vlogId: UUID,
        @Body updatedVlog: VlogEntity
    ): Completable

    @GET("vlog/for-user/{userId}")
    fun getVlogsForUser(
        @Path("userId") userId: UUID,
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<VlogEntity>>

    @GET("vlog/recommended")
    fun getRecommendedVlogs(
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<VlogEntity>>

    @POST("vlog")
    fun postVlog(@Body newVlog: VlogEntity): Completable
}
