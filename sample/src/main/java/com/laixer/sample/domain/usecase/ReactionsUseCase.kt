package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.Reaction
import com.laixer.sample.domain.repository.ReactionRepository
import io.reactivex.Single

class ReactionsUseCase constructor(private val reactionRepository: ReactionRepository) {

    fun get(reactionId: String, refresh: Boolean): Single<List<Reaction>> =
        reactionRepository.get(reactionId, refresh)
}
