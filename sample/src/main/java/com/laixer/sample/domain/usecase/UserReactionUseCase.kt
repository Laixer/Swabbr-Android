package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.Reaction
import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.ReactionRepository
import com.laixer.sample.domain.repository.UserRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * The standard library provides Pair and Triple.
 * In most cases, though, named data classes are a better design choice.
 * This is because they make the code more readable by providing meaningful names for properties.
 */
data class CombinedUserReaction(val user: User, val reaction: Reaction)

class UserReactionUseCase constructor(
    private val userRepository: UserRepository,
    private val reactionRepository: ReactionRepository
) {

    fun get(vlogId: String, refresh: Boolean): Single<List<CombinedUserReaction>> =
        Single.zip(userRepository.get(refresh), reactionRepository.get(vlogId, refresh),
            BiFunction { userList, reactionList -> map(userList, reactionList) })
}

//class UserReactionUseCase constructor(
//    private val userRepository: UserRepository,
//    private val reactionRepository: ReactionRepository
//) {
//
//    fun get(userId: String, vlogId: String, refresh: Boolean): Single<CombinedUserReaction> =
//        Single.zip(userRepository.get(userId, refresh), reactionRepository.get(vlogId, refresh),
//            BiFunction { user, reactionList -> map(user, reaction) })
//}

/**
 * To obtain the user from a reaction we need to use the userId from the reaction to find it in the user list.
 * This is a limitation that comes from the network API and this specific use case requires both sample and users.
 */
fun map(user: User, reaction: Reaction): CombinedUserReaction = CombinedUserReaction(user, reaction)

fun map(userList: List<User>, reactionList: List<Reaction>): List<CombinedUserReaction> =
    reactionList.map { reaction -> CombinedUserReaction(userList.first { reaction.userId == it.id }, reaction) }
