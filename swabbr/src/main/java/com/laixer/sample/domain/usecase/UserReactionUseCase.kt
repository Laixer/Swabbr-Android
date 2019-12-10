package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * The standard library provides Pair and Triple.
 * In most cases, though, named data classes are a better design choice.
 * This is because they make the code more readable by providing meaningful names for properties.
 */
class UserReactionUseCase constructor(
    private val userRepository: UserRepository,
    private val reactionRepository: ReactionRepository
) {

    fun get(vlogId: String, refresh: Boolean): Single<List<Pair<User, Reaction>>> =
        Single.zip(userRepository.get(refresh), reactionRepository.get(vlogId, refresh),
            BiFunction { userList, reactionList -> map(userList, reactionList) })
}

/**
 * To obtain the user from a reaction we need to use the userId from the reaction to find it in the user list.
 * This is a limitation that comes from the network API and this specific use case requires both sample and users.
 */
fun map(user: User, reaction: Reaction): Pair<User, Reaction> = Pair(user, reaction)

fun map(userList: List<User>, reactionList: List<Reaction>): List<Pair<User, Reaction>> =
    reactionList.map { reaction ->
        Pair(
            userList.first { reaction.userId == it.id },
            reaction
        )
    }
