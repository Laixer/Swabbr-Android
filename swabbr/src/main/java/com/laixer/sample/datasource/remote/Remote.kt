package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.AuthenticatedUserEntity
import com.laixer.swabbr.datasource.model.FollowStatusEntity
import com.laixer.swabbr.datasource.model.LoginEntity
import com.laixer.swabbr.datasource.model.ReactionEntity
import com.laixer.swabbr.datasource.model.RegistrationEntity
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.VlogEntity
import com.laixer.swabbr.datasource.model.SettingsEntity
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VlogsApi {

    @GET("vlogs/")
    fun getVlogs(): Single<List<VlogEntity>>

    @GET("vlogs/{id}")
    fun getVlog(@Path("id") vlogId: String): Single<VlogEntity>
}

interface UsersApi {

    @GET("users/")
    fun getUsers(): Single<List<UserEntity>>

    @GET("users/{id}")
    fun getUser(@Path("id") userId: String): Single<UserEntity>
}

interface ReactionsApi {

    @GET("reactions/")
    fun getReactions(@Query("vlogId") vlogId: String): Single<List<ReactionEntity>>
}

interface FollowApi {
    @GET("followStatus/{id}")
    fun getFollowStatus(@Path("id") id: String): Single<FollowStatusEntity>

    @GET("followers/{id}")
    fun getFollowers(@Path("id") id: String): Single<List<UserEntity>>

    @GET("following/{id}")
    fun getFollowing(@Path("id") id: String): Single<List<UserEntity>>

    @GET("incomingRequests/")
    fun getIncomingRequests(): Single<List<UserEntity>>

    @POST("sendFollowRequest/{id}")
    fun sendFollowRequest(@Path("id") id: String): Single<FollowStatusEntity>

    @POST("cancelFollowRequest/{id}")
    fun cancelFollowRequest(@Path("id") id: String): Single<FollowStatusEntity>

    @POST("unfollow/{id}")
    fun unfollow(@Path("id") id: String): Single<FollowStatusEntity>

    @POST("acceptRequest/{id}")
    fun acceptRequest(@Path("id") id: String): Single<FollowStatusEntity>

    @POST("declineRequest/{id}")
    fun declineRequest(@Path("id") id: String): Single<FollowStatusEntity>
}

interface SettingsApi {

    @GET("userSettings/")
    fun get(): Single<SettingsEntity>

    @POST("userSettings/")
    fun set(settings: SettingsEntity): Single<SettingsEntity>
}

interface AuthApi {
//    @GET("login/")
//    fun login(login: LoginEntity): Single<AuthenticatedUserEntity>
    @GET("login/")
    fun login(): Single<AuthenticatedUserEntity>

    // POST
//    @GET("register/")
//    fun register(registration: RegistrationEntity): Single<AuthenticatedUserEntity>
    @GET("register/")
    fun register(): Single<AuthenticatedUserEntity>
}
