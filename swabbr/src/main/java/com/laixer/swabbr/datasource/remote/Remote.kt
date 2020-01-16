package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.AuthUserEntity
import com.laixer.swabbr.datasource.model.FollowRequestEntity
import com.laixer.swabbr.datasource.model.LoginEntity
import com.laixer.swabbr.datasource.model.ReactionEntity
import com.laixer.swabbr.datasource.model.RegistrationEntity
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.VlogEntity
import com.laixer.swabbr.datasource.model.SettingsEntity
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

interface VlogsApi {

    @GET("/api/v1/vlogs/users/{userId}")
    fun getUserVlogs(@Path("userId") userId: String): Single<List<VlogEntity>>

    @GET("api/v1/vlogs/{vlogId}")
    fun getVlog(@Path("vlogId") vlogId: String): Single<VlogEntity>

    @GET("api/v1/vlogs/featured")
    fun getFeaturedVlogs(): Single<List<VlogEntity>>
}

interface UsersApi {

    @GET("api/v1/users/")
    fun getUsers(): Single<List<UserEntity>>

    @GET("/api/v1/users/{userId}")
    fun getUser(@Path("userId") userId: String): Single<UserEntity>

    @GET("/api/v1/users/search")
    fun searchUser(@Query("query") userId: String): Single<List<UserEntity>>
}

interface ReactionsApi {

    @GET("reactions/")
    fun getReactions(@Query("vlogId") vlogId: String): Single<List<ReactionEntity>>
}

interface FollowApi {
    @GET("/api/v1/followrequests/outgoing/{receiverId}")
    fun getFollowRequest(@Path("receiverId") id: String): Single<FollowRequestEntity>

    @GET("/api/v1/users/{userId}/followers")
    fun getFollowers(@Path("userId") id: String): Single<List<UserEntity>>

    @GET("/api/v1/users/{userId}/following")
    fun getFollowing(@Path("userId") id: String): Single<List<UserEntity>>

    @GET("/api/v1/followrequests/incoming")
    fun getIncomingRequests(): Single<List<UserEntity>>

    @POST("/api/v1/followrequests/send")
    fun sendFollowRequest(@Query("receiverId") userId: String): Single<FollowRequestEntity>

    @DELETE("/api/v1/followrequests/{followRequestId}/cancel")
    fun cancelFollowRequest(@Path("followRequestId") id: String): Single<FollowRequestEntity>

    @DELETE("/api/v1/users/{userId}/unfollow")
    fun unfollow(@Path("userId") id: String): Single<FollowRequestEntity>

    @PUT("/api/v1/followrequests/{followRequestId}/accept")
    fun acceptRequest(@Path("followRequestId") id: String): Single<FollowRequestEntity>

    @PUT("/api/v1/followrequests/{followRequestId}/decline")
    fun declineRequest(@Path("followRequestId") id: String): Single<FollowRequestEntity>
}

interface SettingsApi {

    @GET("/api/v1/users/self/settings/get")
    fun get(): Single<SettingsEntity>

    @PUT("/api/v1/users/self/settings/update/")
    fun set(@Body settings: SettingsEntity): Single<SettingsEntity>
}

interface AuthApi {
    @POST("api/v1/authentication/login/")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<AuthUserEntity>

    @POST("api/v1/authentication/register/")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Single<AuthUserEntity>

    @DELETE("api/v1/authentication/logout/")
    fun logout(): Completable
}
