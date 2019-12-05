package com.laixer.sample.data.datasource

import io.reactivex.Single

interface FollowRemoteDataSource {

    fun getFollowStatus(targetId: String): Single<String>

    //fun getFollowers(userId: String): Single<List<User>>

    //fun set(followRequest: FollowRequest): Single<FollowRequest>

}
