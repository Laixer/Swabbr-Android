package com.laixer.sample.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.sample.domain.usecase.UsersUseCase
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel constructor(private val usersUseCase: UsersUseCase) :
    ViewModel() {

    val users = MutableLiveData<Resource<List<ProfileItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun get(query: String) =
        compositeDisposable.add(
            usersUseCase.get(query, refresh = false)
                .doOnSubscribe { users.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe({ users.setSuccess(listOf(it)) }, { users.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
