package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 *  Interface for user related API calls.
 */
interface UserApi {
    @GET("user/{userId}")
    fun getUser(@Path("userId") userId: UUID): Single<UserEntity>

    @GET("user/{userId}/statistics")
    fun getWithStats(@Path("userId") userId: UUID): Single<UserWithStatsEntity>

    @GET("user/self/statistics")
    fun getSelfWithStats(): Single<UserWithStatsEntity>

    @GET("user/{userId}/following")
    fun getFollowing(
        @Path("userId") userId: UUID,
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserEntity>>

    @GET("user/{userId}/followers")
    fun getFollowers(
        @Path("userId") userId: UUID,
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserEntity>>

    @GET("user/follow-requesting-users")
    fun getFollowRequestingUsers(
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserWithRelationEntity>>

    @GET("user/vlog-liking-users")
    fun getVlogLikingUsers(
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<VlogLikingUserWrapperEntity>>

    @GET("user/self")
    fun getSelf(): Single<UserCompleteEntity>

    @GET("user/search")
    fun search(
        @Query("query") query: String,
        @Query("sortingOrder") sortingOrder: Int?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserWithRelationEntity>>

    @PUT("user")
    fun update(@Body updatedUser: UserUpdateEntity): Completable
}
