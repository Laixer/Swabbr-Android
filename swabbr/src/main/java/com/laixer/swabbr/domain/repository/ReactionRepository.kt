package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single

interface ReactionRepository {

    fun get(vlogId: String, refresh: Boolean): Single<List<Reaction>>
}
