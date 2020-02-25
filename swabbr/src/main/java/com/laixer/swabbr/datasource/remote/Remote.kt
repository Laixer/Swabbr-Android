package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.AuthUserEntity
import com.laixer.swabbr.datasource.model.FollowRequestEntity
import com.laixer.swabbr.datasource.model.LoginEntity
import com.laixer.swabbr.datasource.model.ReactionEntity
import com.laixer.swabbr.datasource.model.RegistrationEntity
import com.laixer.swabbr.datasource.model.SettingsEntity
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.VlogEntity
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

    @GET("vlogs/users/{userId}")
    fun getUserVlogs(@Path("userId") userId: String): Single<List<VlogEntity>>

    @GET("vlogs/{vlogId}")
    fun getVlog(@Path("vlogId") vlogId: String): Single<VlogEntity>

    @GET("vlogs/featured")
    fun getFeaturedVlogs(): Single<List<VlogEntity>>
}

interface UsersApi {

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: String): Single<UserEntity>

    @GET("users")
    fun searchUserByFirstname(@Query("firstName_like") name: String): Single<List<UserEntity>>
}

interface ReactionsApi {

    @GET("reactions/")
    fun getReactions(@Query("vlogId") vlogId: String): Single<List<ReactionEntity>>
}

interface FollowApi {
    @GET("followrequests/outgoing/{receiverId}")
    fun getFollowRequest(@Path("receiverId") id: String): Single<FollowRequestEntity>

    @GET("users/{userId}/followers")
    fun getFollowers(@Path("userId") id: String): Single<List<UserEntity>>

    @GET("users/{userId}/following")
    fun getFollowing(@Path("userId") id: String): Single<List<UserEntity>>

    @GET("followrequests/incoming")
    fun getIncomingRequests(): Single<List<UserEntity>>

    @POST("followrequests/send")
    fun sendFollowRequest(@Query("receiverId") userId: String): Single<FollowRequestEntity>

    @DELETE("followrequests/{followRequestId}/cancel")
    fun cancelFollowRequest(@Path("followRequestId") id: String): Single<FollowRequestEntity>

    @DELETE("users/{userId}/unfollow")
    fun unfollow(@Path("userId") id: String): Single<FollowRequestEntity>

    @PUT("followrequests/{followRequestId}/accept")
    fun acceptRequest(@Path("followRequestId") id: String): Single<FollowRequestEntity>

    @PUT("followrequests/{followRequestId}/decline")
    fun declineRequest(@Path("followRequestId") id: String): Single<FollowRequestEntity>
}

interface SettingsApi {

    @GET("users/self/settings/get")
    fun get(): Single<SettingsEntity>

    @PUT("users/self/settings/update/")
    fun set(@Body settings: SettingsEntity): Single<SettingsEntity>
}

interface AuthApi {
    @POST("authentication/login/")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<AuthUserEntity>

    @POST("authentication/register/")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Single<AuthUserEntity>

    @DELETE("authentication/logout/")
    fun logout(): Completable
}
