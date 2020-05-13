package com.laixer.swabbr.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel constructor(private val usersUseCase: UsersUseCase) : ViewModel() {

    val profiles = MutableLiveData<Resource<List<UserItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun search(query: String?, page: Int = 1, itemsPerPage: Int = 50, refreshList: Boolean = false) =
        compositeDisposable
            .add(usersUseCase.search(query, page, itemsPerPage)
                .doOnSubscribe { profiles.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    {
                        profiles.setSuccess(
                            if (refreshList) {
                                it
                            } else {
                                profiles.value?.data?.plus(it) ?: it
                            }
                        )
                    },
                    { profiles.setError(it.message) })
            )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
