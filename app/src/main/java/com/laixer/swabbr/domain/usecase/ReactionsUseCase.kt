package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.repository.ReactionRepository
import io.reactivex.Single
import java.util.UUID

class ReactionsUseCase constructor(private val reactionRepository: ReactionRepository) {

    fun get(reactionId: UUID, refresh: Boolean): Single<List<Reaction>> = reactionRepository.get(reactionId, refresh)
}
