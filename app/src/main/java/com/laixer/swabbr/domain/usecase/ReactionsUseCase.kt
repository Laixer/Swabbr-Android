package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.repository.ReactionRepository
import io.reactivex.Single
import java.util.UUID

// TODO Post reaction here?
/**
 *  Use case with regards to getting reactions.
 */
class ReactionsUseCase constructor(private val reactionRepository: ReactionRepository) {
    // TODO Pass force refresh
    fun get(reactionId: UUID, refresh: Boolean): Single<Reaction> = reactionRepository.get(reactionId)

    // TODO Pass force refresh
    fun getForVlog(vlogId: UUID, refresh: Boolean): Single<List<Reaction>> = reactionRepository.getForVlog(vlogId)
}
