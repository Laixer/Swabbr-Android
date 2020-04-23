package com.laixer.swabbr.datasource.model.remote

import com.laixer.swabbr.datasource.model.AuthUserEntity
import com.laixer.swabbr.datasource.model.FollowRequestEntity
import com.laixer.swabbr.datasource.model.LoginEntity
import com.laixer.swabbr.datasource.model.ReactionEntity
import com.laixer.swabbr.datasource.model.RegistrationEntity
import com.laixer.swabbr.datasource.model.SettingsEntity
import com.laixer.swabbr.datasource.model.UserEntity
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
    fun getRecommendedVlogs(): Single<List<VlogEntity>>
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
}

interface ReactionsApi {

    @GET("reactions/")
    fun getReactions(@Query("vlogId") vlogId: UUID): Single<List<ReactionEntity>>
}

interface FollowApi {
    @GET("followrequests/outgoing/{receiverId}")
    fun getFollowRequest(@Path("receiverId") id: UUID): Single<FollowRequestEntity>

    @GET("users/{userId}/followers")
    fun getFollowers(@Path("userId") id: UUID): Single<List<UserEntity>>

    @GET("users/{userId}/following")
    fun getFollowing(@Path("userId") id: UUID): Single<List<UserEntity>>

    @GET("followrequests/incoming")
    fun getIncomingRequests(): Single<List<UserEntity>>

    @POST("followrequests/send")
    fun sendFollowRequest(@Query("receiverId") userId: UUID): Single<FollowRequestEntity>

    @DELETE("followrequests/{followRequestId}/cancel")
    fun cancelFollowRequest(@Path("followRequestId") id: UUID): Single<FollowRequestEntity>

    @DELETE("users/{userId}/unfollow")
    fun unfollow(@Path("userId") id: UUID): Single<FollowRequestEntity>

    @PUT("followrequests/{followRequestId}/accept")
    fun acceptRequest(@Path("followRequestId") id: UUID): Single<FollowRequestEntity>

    @PUT("followrequests/{followRequestId}/decline")
    fun declineRequest(@Path("followRequestId") id: UUID): Single<FollowRequestEntity>
}

interface SettingsApi {

    @GET("users/self/settings/get")
    fun get(): Single<SettingsEntity>

    @PUT("users/self/settings/update")
    fun set(@Body settings: SettingsEntity): Single<SettingsEntity>
}

interface AuthApi {
    @POST("authentication/login")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<AuthUserEntity>

    @POST("authentication/register")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Single<AuthUserEntity>

    @DELETE("authentication/logout")
    fun logout(): Completable
}
