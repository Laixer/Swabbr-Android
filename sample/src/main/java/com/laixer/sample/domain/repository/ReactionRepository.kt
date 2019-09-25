package com.laixer.sample.domain.repository

import com.laixer.sample.domain.model.Reaction
import io.reactivex.Single

interface ReactionRepository {

    fun get(vlogId: String, refresh: Boolean): Single<List<Reaction>>
}
