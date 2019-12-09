package com.laixer.sample.datasource.remote

import com.laixer.sample.datasource.model.FollowStatusEntity
import com.laixer.sample.datasource.model.ReactionEntity
import com.laixer.sample.datasource.model.UserEntity
import com.laixer.sample.datasource.model.VlogEntity
import io.reactivex.Single
import retrofit2.http.GET
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
}
