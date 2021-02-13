package com.laixer.swabbr.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.types.Pagination
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.extensions.cascadeFollowAction
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model containing user searching functionality.
 */
class SearchViewModel constructor(
    private val usersUseCase: UsersUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {
    /**
     *  Resource in which the search results are stored.
     */
    val users = MutableLiveData<Resource<List<UserWithRelationItem>>>()

    // Displays how many items the previous search query returned.
    var lastQueryResultCount = 0

    // Used for resource disposal.
    private val compositeDisposable = CompositeDisposable()

    /**
     *  Search for users and store the result in [users].
     *
     *  @param query Search string.
     *  @param pagination Result set control.
     *  @param refreshList True if we want to override the list,
     *                     false if we want to append to the list.
     */
    fun search(query: String, pagination: Pagination = Pagination.latest(), refreshList: Boolean = false) =
        compositeDisposable
            .add(usersUseCase.search(query, pagination)
                .doOnSubscribe { users.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    {
                        lastQueryResultCount = it.size

                        // Either override or append to the current list.
                        users.setSuccess(
                            if (refreshList) {
                                it
                            } else {
                                users.value?.data?.plus(it) ?: it
                            }
                        )
                    },
                    {
                        users.setError(it.message)
                    })
            )

    // TODO Duplicate follow-based functionality with [LikeOverviewViewModel].
    /**
     *  Sends a follow request from the current user to a given user
     *  id. The follow request is then cascaded into [users].
     *
     *  @param userId The user to follow.
     */
    fun follow(userId: UUID) {
        // First update locally, then perform the action.
        users.cascadeFollowAction(userId, FollowRequestStatus.PENDING)

        compositeDisposable.add(
            followUseCase.sendFollowRequest(userId)
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    /**
     *  Clears the current search results.
     */
    fun clearSearchResults() = users.setSuccess(emptyList())

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
