package com.laixer.sample.domain.repository

import io.reactivex.Single

interface FollowRepository {

    fun getFollowStatus(targetId: String): Single<String>
}
