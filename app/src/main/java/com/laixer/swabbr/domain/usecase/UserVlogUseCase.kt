package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Like
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.UUID

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

    fun getRecommendedVlogs(refresh: Boolean): Single<List<Pair<User, Vlog>>> =
        vlogRepository.getRecommendedVlogs(refresh).flattenAsObservable { vlogs -> vlogs }.flatMapSingle { vlog ->
            userRepository.get(vlog.userId, refresh).map { user -> Pair(user, vlog) }
        }.toList()

    /**
     * For a list of vlog ids, return those vlogs paired with the user who posted them.
     */
    fun get(idList: List<UUID>, refresh: Boolean): Single<List<Pair<User, Vlog>>> =
        Observable.just(idList).flatMapIterable { ids -> ids }.flatMapSingle { id -> vlogRepository.get(id, refresh) }
            .flatMapSingle { vlog ->
                userRepository.get(vlog.userId, refresh).map { user -> Pair(user, vlog) }
            }.toList()
}

class UserVlogUseCase constructor(
    private val userRepository: UserRepository,
    private val vlogRepository: VlogRepository
) {

    fun get(vlogId: UUID, refresh: Boolean): Single<Pair<User, Vlog>> =
        vlogRepository.get(vlogId, refresh).flatMap { vlog ->
            userRepository.get(vlog.userId, refresh).map { user -> Pair(user, vlog) }
        }
}

class UserVlogsUseCase constructor(private val vlogRepository: VlogRepository) {

    fun get(userId: UUID, refresh: Boolean): Single<List<Vlog>> = vlogRepository.getUserVlogs(userId, refresh)
}

class VlogsUseCase constructor(private val vlogRepository: VlogRepository) {
    fun getLikes(vlogId: UUID, refresh: Boolean): Single<List<Like>> = vlogRepository.getLikes(vlogId, refresh)

    fun like(vlogId: UUID): Completable = vlogRepository.like(vlogId)

    fun unlike(vlogId: UUID): Completable = vlogRepository.unlike(vlogId)
}

/**
 * To obtain the user from a vlog we need to use the userId from the vlog to find it in the user list.
 * This is a limitation that comes from the network API and this specific use case requires both reactions and users.
 */
fun map(userList: List<User>, vlog: Vlog): Pair<User, Vlog> = Pair(userList.first { vlog.userId == it.id }, vlog)

fun map(userList: List<User>, vlogList: List<Vlog>): List<Pair<User, Vlog>> =
    vlogList.map { vlog -> Pair(userList.first { vlog.userId == it.id }, vlog) }