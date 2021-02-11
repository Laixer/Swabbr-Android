package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.interfaces.ReactionRepository
import com.laixer.swabbr.domain.interfaces.UserRepository
import com.laixer.swabbr.domain.types.ReactionWrapper
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO Pass refresh.
/**
 *  Use case for processing reaction related operations. Often the
 *  reaction with its user is required. This use case bundles these
 *  using a [ReactionWrapper].
 */
class ReactionUseCase constructor(
    private val userRepository: UserRepository,
    private val reactionRepository: ReactionRepository
) {
    /**
     *  Deletes a reaction. This will only work if we own the reaction.
     *
     *  @param reactionId The reaction to delete.
     */
    fun deleteReaction(reactionId: UUID) : Completable = reactionRepository.delete(reactionId)

    /**
     *  Gets a single reaction wrapper.
     *
     *  @param reactionId The id of the reaction.
     */
    fun get(reactionId: UUID): Single<ReactionWrapper> =
        reactionRepository.get(reactionId)
            .flatMap { reaction ->
                userRepository
                    .get(reaction.userId, false)
                    .map { user ->
                        ReactionWrapper(
                            reaction = reaction,
                            user = user
                        )
                    }
            }

    /**
     *  Get all reactions for a given vlog with the user that posted
     *  these reactions in a wrapper.
     *
     *  @param vlogId The vlog to get reactions for.
     *  @param refresh Force a refresh of the data.
     */
    fun getAllForVlog(vlogId: UUID, refresh: Boolean): Single<List<ReactionWrapper>> =
        reactionRepository.getForVlog(vlogId)
            .flattenAsObservable { reactions -> reactions }
            .flatMapSingle { reaction ->
                userRepository
                    .get(reaction.userId, false)
                    .map { user ->
                        ReactionWrapper(
                            reaction = reaction,
                            user = user
                        )
                    }
            }
            .toList()

    /**
     *  Generates a new upload wrapper for a reaction.
     *
     *  @return Wrapper also containing the id of the reaction.
     */
    fun generateUploadWrapper(): Single<UploadWrapper> = reactionRepository.generateUploadWrapper()

    /**
     *  Posts a new reaction to the backend. The reaction
     *  should already be uploaded to the blob storage along
     *  with its thumbnail when calling this function.
     *
     *  @param reaction The reaction we wish to post.
     */
    fun postReaction(reaction: Reaction): Completable = reactionRepository.post(reaction)
}
