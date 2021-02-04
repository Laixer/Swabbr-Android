package com.laixer.swabbr.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.Pagination
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 *  Viewmodel containing user searching functionality.
 */
class SearchViewModel constructor(private val usersUseCase: UsersUseCase) : ViewModel() {

    val profiles = MutableLiveData<Resource<List<UserItem>>>()
    var lastQueryResultCount = 0
    private val compositeDisposable = CompositeDisposable()

    fun search(query: String, pagination: Pagination = Pagination.latest(), refreshList: Boolean = false) =
        compositeDisposable
            .add(usersUseCase.search(query, pagination)
                .doOnSubscribe { profiles.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    {
                        lastQueryResultCount = it.size
                        profiles.setSuccess(
                            if (refreshList) {
                                it
                            } else {
                                profiles.value?.data?.plus(it) ?: it
                            }
                        )
                    },
                    {
                        profiles.setError(it.message)
                    })
            )

    /**
     *  Clears the current search results.
     */
    fun clearSearchResults() = profiles.setSuccess(emptyList())

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
