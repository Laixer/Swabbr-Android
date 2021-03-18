package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 *  Interface for follow request related API calls.
 */
interface FollowRequestApi {
    @GET("followrequest")
    fun get(
        @Query("requesterId") requesterId: UUID,
        @Query("receiverId") receiverId: UUID
    ): Single<FollowRequestEntity>

    @GET("followrequest/incoming")
    fun getIncomingRequests(
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<FollowRequestEntity>>

    @GET("followrequest/outgoing")
    fun getOutgoingRequests(
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<FollowRequestEntity>>

    @POST("followrequest")
    fun sendFollowRequest(@Query("receiverId") userId: UUID): Completable

    @PUT("followrequest/cancel")
    fun cancelFollowRequest(@Query("receiverId") receiverId: UUID): Completable

    @POST("followrequest/unfollow")
    fun unfollow(@Query("receiverId") receiverId: UUID): Completable

    @PUT("followrequest/accept")
    fun acceptRequest(@Query("requesterId") requesterId: UUID): Completable

    @PUT("followrequest/decline")
    fun declineRequest(@Query("requesterId") requesterId: UUID): Completable
}
