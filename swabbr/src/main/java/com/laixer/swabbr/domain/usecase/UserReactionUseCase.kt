package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single

/**
 * The standard library provides Pair and Triple.
 * In most cases, though, named data classes are a better design choice.
 * This is because they make the code more readable by providing meaningful names for properties.
 */
class UserReactionUseCase constructor(
    private val userRepository: UserRepository,
    private val reactionRepository: ReactionRepository
) {

    /**
     * For a specified vlog, get a list of all reactions paired with the user who posted them
     */
    fun get(vlogId: String, refresh: Boolean): Single<List<Pair<User, Reaction>>> =
        reactionRepository.get(vlogId, refresh)
            .flattenAsObservable { reactions -> reactions }
            .flatMapSingle { reaction ->
                userRepository.get(reaction.userId, false)
                    .map { user -> Pair(user, reaction) }
            }.toList()
}

/**
 * To obtain the user from a reaction we need to use the userId from the reaction to find it in the user list.
 * This is a limitation that comes from the network API and this specific use case requires both reactions and users.
 */
fun map(user: User, reaction: Reaction): Pair<User, Reaction> = Pair(user, reaction)

fun map(userList: List<User>, reactionList: List<Reaction>): List<Pair<User, Reaction>> =
    reactionList.map { reaction ->
        Pair(
            userList.first { reaction.userId == it.id },
            reaction
        )
    }
