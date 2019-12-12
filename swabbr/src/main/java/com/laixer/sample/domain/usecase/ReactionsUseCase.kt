package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.repository.ReactionRepository
import io.reactivex.Single

class ReactionsUseCase constructor(private val reactionRepository: ReactionRepository) {

    fun get(reactionId: String, refresh: Boolean): Single<List<Reaction>> =
        reactionRepository.get(reactionId, refresh)
}