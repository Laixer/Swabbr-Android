package com.laixer.swabbr.datasource.model.remote

import com.laixer.swabbr.datasource.model.AuthUserEntity
import com.laixer.swabbr.datasource.model.FollowRequestEntity
import com.laixer.swabbr.datasource.model.FollowStatusEntity
import com.laixer.swabbr.datasource.model.LoginEntity
import com.laixer.swabbr.datasource.model.ReactionEntity
import com.laixer.swabbr.datasource.model.RegistrationEntity
import com.laixer.swabbr.datasource.model.SettingsEntity
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.UserStatisticsEntity
import com.laixer.swabbr.datasource.model.VlogEntity
import com.laixer.swabbr.datasource.model.VlogListResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface VlogsApi {

    @GET("vlogs/foruser/{userId}")
    fun getUserVlogs(@Path("userId") userId: UUID): Single<VlogListResponse>

    @GET("vlogs/{vlogId}")
    fun getVlog(@Path("vlogId") vlogId: UUID): Single<VlogEntity>

    @GET("vlogs/recommended")
    fun getRecommendedVlogs(): Single<VlogListResponse>
}

interface UsersApi {

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: UUID): Single<UserEntity>

    @GET("users/search")
    fun searchUserByFirstname(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("itemsPerPage") itemsPerPage: Int = 50
    ): Single<List<UserEntity>>

    @POST("users/self")
    fun self(): Single<UserEntity>

    @POST("users/update")
    fun update(@Body updatedUser: UserEntity): Single<UserEntity>

    @GET("users/{userId}/following")
    fun getFollowing(@Path("userId") id: UUID): Single<List<UserEntity>>

    @GET("users/{userId}/followers")
    fun getFollowers(@Path("userId") id: UUID): Single<List<UserEntity>>

    @GET("users/{userId}}/statistics")
    fun getStatistics(@Path("userId") id: UUID): Single<UserStatisticsEntity>

    @GET("users/self/statistics")
    fun getSelfStatistics(): Single<UserEntity>
}

interface ReactionsApi {

    @GET("reactions/")
    fun getReactions(@Query("vlogId") vlogId: UUID): Single<List<ReactionEntity>>
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
}

interface SettingsApi {

    @GET("users/self/settings")
    fun get(): Single<SettingsEntity>

    @POST("users/self/settings")
    fun set(@Body settings: SettingsEntity): Completable
}

interface AuthApi {
    @POST("authentication/login")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<AuthUserEntity>

    @POST("authentication/register")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Single<AuthUserEntity>

    @POST("authentication/logout")
    fun logout(): Completable
}
