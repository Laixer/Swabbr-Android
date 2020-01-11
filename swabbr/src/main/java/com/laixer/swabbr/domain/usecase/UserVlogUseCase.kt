package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * The standard library provides Pair and Triple.
 * In most cases, though, named data classes are a better design choice.
 * This is because they make the code more readable by providing meaningful names for properties.
 */
data class CombinedUserVlog(val user: User, val vlog: Vlog)

class UsersVlogsUseCase constructor(
    private val userRepository: UserRepository,
    private val vlogRepository: VlogRepository
) {

    fun get(refresh: Boolean): Single<List<Pair<User, Vlog>>> =
        Single.zip(userRepository.get(refresh), vlogRepository.get(refresh),
            BiFunction { userList, vlogList -> map(userList, vlogList) })

    fun get(ids: ArrayList<String>, refresh: Boolean): Single<List<Pair<User, Vlog>>> =
        Single.zip(userRepository.get(refresh), vlogRepository.get(refresh),
            BiFunction { user, vlog -> map(user, vlog.filter { ids.contains(it.vlogId) }) })

    fun getFeaturedVlogs(): Single<List<Pair<User, Vlog>>> =
        vlogRepository.getFeaturedVlogs().flattenAsObservable {vlogs -> vlogs}
            .flatMapSingle{vlog -> userRepository.get(vlog.userId, false)
                .map { user -> Pair(user, vlog)}
            }.toList()
}

class UserVlogUseCase constructor(
    private val userRepository: UserRepository,
    private val vlogRepository: VlogRepository
) {
    fun get(vlogId: String, refresh: Boolean): Single<Pair<User, Vlog>> =
        Single.zip(
            userRepository.get(refresh),
            vlogRepository.get(vlogId, refresh),
            BiFunction { user, vlog -> map(user, vlog) })
}

class UserVlogsUseCase constructor(
    private val userRepository: UserRepository,
    private val vlogRepository: VlogRepository
) {

    fun get(userId: String, refresh: Boolean): Single<Pair<User, List<Vlog>>> =
        Single.zip(
            userRepository.get(userId, refresh),
            vlogRepository.get(refresh),
            BiFunction { user, vlogs -> Pair(user, vlogs.filter { it.userId == userId }) }
        )
}

/**
 * To obtain the user from a vlog we need to use the userId from the vlog to find it in the user list.
 * This is a limitation that comes from the network API and this specific use case requires both reactions and users.
 */
fun map(userList: List<User>, vlog: Vlog): Pair<User, Vlog> =
    Pair(userList.first { vlog.userId == it.id }, vlog)

fun map(userList: List<User>, vlogList: List<Vlog>): List<Pair<User, Vlog>> =
    vlogList.map { vlog -> Pair(userList.first { vlog.userId == it.id }, vlog) }
