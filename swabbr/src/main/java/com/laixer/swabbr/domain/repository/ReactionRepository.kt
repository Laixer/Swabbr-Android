package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single
import java.util.UUID

interface ReactionRepository {

    fun get(vlogId: UUID, refresh: Boolean): Single<List<Reaction>>
}
