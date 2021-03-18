package com.laixer.swabbr.presentation.search

import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.Pagination
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.abstraction.UserWithRelationListViewModelBase
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.schedulers.Schedulers

/**
 *  View model containing user searching functionality.
 */
class SearchViewModel constructor(
    private val usersUseCase: UsersUseCase,
    followUseCase: FollowUseCase
) : UserWithRelationListViewModelBase(followUseCase) {
    // Displays how many items the previous search query returned.
    var lastQueryResultCount = 0

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

    /**
     *  Clears the current search results.
     */
    fun clearSearchResults() = users.setSuccess(emptyList())
}
