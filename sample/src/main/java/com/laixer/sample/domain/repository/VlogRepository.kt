package com.laixer.sample.domain.repository

import com.laixer.sample.domain.model.Vlog
import io.reactivex.Single

interface VlogRepository {

    fun get(refresh: Boolean): Single<List<Vlog>>

    fun get(vlogId: String, refresh: Boolean): Single<Vlog>
}
