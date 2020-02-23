package com.laixer.swabbr.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel constructor(private val usersUseCase: UsersUseCase) :
    ViewModel() {

    val profiles = MutableLiveData<Resource<List<ProfileItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun getProfiles(query: String) =
        compositeDisposable.add(
            usersUseCase.search(query)
                .doOnSubscribe { profiles.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe(
                    { profiles.setSuccess(it) },
                    { profiles.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
