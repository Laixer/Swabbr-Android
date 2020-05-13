package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface FollowRemoteDataSource {

    fun getFollowStatus(userId: UUID): Single<FollowStatus>

    fun getIncomingRequests(): Single<List<FollowRequest>>

    fun getOutgoingRequests(): Single<List<FollowRequest>>

    fun sendFollowRequest(userId: UUID): Single<FollowRequest>

    fun cancelFollowRequest(userId: UUID): Completable

    fun unfollow(userId: UUID): Completable

    fun acceptRequest(userId: UUID): Single<FollowRequest>

    fun declineRequest(userId: UUID): Single<FollowRequest>

    fun getFollowers(userId: UUID): Single<List<User>>

    fun getFollowing(userId: UUID): Single<List<User>>

}

interface FollowCacheDataSource {
    fun getFollowingKey(userId: UUID) = "FOLLOWS_$userId"
    fun getFollowersKey(userId: UUID) = "FOLLOWERS_$userId"

    fun getFollowers(userId: UUID): Single<List<User>>

    fun setFollowers(userId: UUID, users: List<User>): Single<List<User>>

    fun getFollowing(userId: UUID): Single<List<User>>

    fun setFollowing(userId: UUID, users: List<User>): Single<List<User>>
}
