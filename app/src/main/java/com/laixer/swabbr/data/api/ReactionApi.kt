package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 *  Interface for reaction related API calls.
 */
interface ReactionApi {
    @DELETE("reaction/{reactionId}")
    fun delete(@Path("reactionId") reactionId: UUID): Completable

    @GET("reaction/generate-upload-uri")
    fun generateUploadWrapper(): Single<UploadWrapperEntity>

    @GET("reaction/{reactionId}")
    fun getReaction(@Path("reactionId") reactionId: UUID): Single<ReactionEntity>

    @PUT("reaction")
    fun updateReaction(@Body updatedReaction: ReactionEntity): Completable

    @GET("reaction/for-vlog/{vlogId}")
    fun getReactionsForVlog(
        @Path("vlogId") vlogId: UUID,
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<ReactionEntity>>

    @GET("reaction/for-vlog/{vlogId}/count")
    fun getReactionCountForVlog(@Path("vlogId") vlogId: UUID): Single<DatasetStatsEntity>

    @POST("reaction")
    fun postReaction(@Body newReaction: ReactionEntity): Completable
}
