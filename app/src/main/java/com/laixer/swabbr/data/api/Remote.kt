package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

interface AuthApi {
    @POST("authentication/login")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<TokenWrapperEntity>

    @POST("authentication/register")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Completable

    @POST("authentication/logout")
    fun logout(): Completable
}

interface FollowRequestApi {
    @GET("followrequest")
    fun get(
        @Query("requesterId") requesterId: UUID,
        @Query("receiverId") receiverId: UUID
    ): Single<FollowRequestEntity>

    @GET("followrequest/incoming")
    fun getIncomingRequests(
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<FollowRequestEntity>>

    @GET("followrequest/outgoing")
    fun getOutgoingRequests(
        @Query("sortingOrder") sortingOrder: SortingOrder?,
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
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserEntity>>

    @GET("user/{userId}/followers")
    fun getFollowers(
        @Path("userId") userId: UUID,
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserEntity>>

    @GET("user/search")
    fun search(
        @Query("query") query: String,
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<UserEntity>>

    @GET("user/self")
    fun getSelf(): Single<UserCompleteEntity>

    @PUT("user")
    fun update(@Body updatedUser: UserUpdateEntity): Completable
}


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

    @GET("vlog-like/vlog-liking-users")
    fun getVlogLikingUsers(
        @Query("sortingOrder") sortingOrder: SortingOrder?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Single<List<VlogLikingUserWrapperEntity>>

    @POST("vlog-like/like/{vlogId}")
    fun like(@Path("vlogId") vlogId: UUID): Completable

    @POST("vlog-like/unlike/{vlogId}")
    fun unlike(@Path("vlogId") vlogId: UUID): Completable
}
