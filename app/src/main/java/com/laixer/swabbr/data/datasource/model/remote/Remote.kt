package com.laixer.swabbr.data.datasource.model.remote

import com.laixer.swabbr.data.datasource.model.*
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.UUID

interface VlogsApi {

    @GET("vlogs/foruser/{userId}")
    fun getUserVlogs(@Path("userId") userId: UUID): Single<VlogListResponseEntity>

    @GET("vlogs/{vlogId}")
    fun getVlog(@Path("vlogId") vlogId: UUID): Single<VlogResponseEntity>

    @GET("vlogs/recommended")
    fun getRecommendedVlogs(): Single<VlogListResponseEntity>

    @GET("vlogs/{vlogId}/vlog_likes")
    fun getLikes(@Path("vlogId") vlogId: UUID): Single<LikeListEntity>

    @POST("vlogs/{vlogId}/like")
    fun like(@Path("vlogId") vlogId: UUID): Completable

    @POST("vlogs/{vlogId}/unlike")
    fun unlike(@Path("vlogId") vlogId: UUID): Completable

    @GET("vlogs/{vlogId}/watch")
    fun watch(@Path("vlogId") vlogId: UUID): Single<WatchVlogResponse>

    @DELETE("vlogs/{vlogId}")
    fun delete(@Path("vlogId") vlogId: UUID): Completable
}

interface LivestreamApi {
    @POST("livestreams/{livestreamId}/start_streaming")
    fun startStreaming(@Path("livestreamId") livestreamId: String): Single<StreamResponse>

    @GET("livestreams/{livestreamId}/watch")
    fun watch(@Path("livestreamId") livestreamId: String): Single<WatchLivestreamResponse>
}

interface UsersApi {

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: UUID): Single<UserEntity>

    @GET("users/search")
    fun search(
        @Query("query") query: String?,
        @Query("page") page: Int = 1,
        @Query("itemsPerPage") itemsPerPage: Int = 50
    ): Single<List<UserEntity>>

    @POST("users/update")
    fun update(@Body updatedUser: UserEntity): Single<UserEntity>

    @GET("users/{userId}/statistics")
    fun getStatistics(@Path("userId") id: UUID): Single<UserStatisticsEntity>

    @GET("users/self/statistics")
    fun getSelfStatistics(): Single<UserStatisticsEntity>
}

interface ReactionsApi {

    @GET("reactions/for_vlog/{vlogId}")
    fun getReactions(@Path("vlogId") vlogId: UUID): Single<ReactionListResponse>
}

interface FollowApi {
    @GET("followrequests/outgoing/status")
    fun getFollowStatus(@Query("receiverId") id: UUID): Single<FollowStatusEntity>

    @GET("followrequests/incoming")
    fun getIncomingRequests(): Single<List<FollowRequestEntity>>

    @GET("followrequests/outgoing")
    fun getOutgoingRequests(): Single<List<FollowRequestEntity>>

    @POST("followrequests/send")
    fun sendFollowRequest(@Query("receiverId") userId: UUID): Single<FollowRequestEntity>

    @POST("followrequests/cancel")
    fun cancelFollowRequest(@Query("receiverId") id: UUID): Completable

    @POST("followrequests/unfollow")
    fun unfollow(@Query("receiverId") id: UUID): Completable

    @PUT("followrequests/accept")
    fun acceptRequest(@Query("requesterId") id: UUID): Single<FollowRequestEntity>

    @PUT("followrequests/decline")
    fun declineRequest(@Query("requesterId") id: UUID): Single<FollowRequestEntity>

    @GET("users/{userId}/following")
    fun getFollowing(@Path("userId") id: UUID): Single<List<UserEntity>>

    @GET("users/{userId}/followers")
    fun getFollowers(@Path("userId") id: UUID): Single<List<UserEntity>>
}

interface SettingsApi {

    @GET("users/self/settings")
    fun get(): Single<SettingsEntity>

    @POST("users/self/settings")
    fun set(@Body settings: SettingsEntity): Single<SettingsEntity>
}

interface AuthApi {

    @GET("users/self")
    fun self(): Single<UserEntity>

    @POST("authentication/login")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<AuthUserEntity>

    @POST("authentication/register")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Single<AuthUserEntity>

    @POST("authentication/logout")
    fun logout(): Completable
}
