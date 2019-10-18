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

    val profiles = MutableLiveData<Resource<List<ProfileItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun getProfiles(query: String, refresh: Boolean = false) =
        compositeDisposable.add(
            usersUseCase.get(query, refresh = refresh)
                .doOnSubscribe { profiles.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe({ profiles.setSuccess(listOf(it)) }, { profiles.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
