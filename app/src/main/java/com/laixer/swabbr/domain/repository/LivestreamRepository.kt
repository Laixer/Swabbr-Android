package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.datasource.model.StreamResponse
import com.laixer.swabbr.datasource.model.WatchResponse
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Single

interface LivestreamRepository {

    fun startStreaming(livestreamId: String): Single<StreamResponse>

    fun watch(livestreamId: String): Single<WatchResponse>
}
