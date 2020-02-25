package com.laixer.swabbr.presentation.vloglist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.presentation.model.ProfileVlogItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VlogListViewModel constructor(private val usersVlogsUseCase: UsersVlogsUseCase) :
    ViewModel() {

    val vlogs = MutableLiveData<Resource<List<ProfileVlogItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun get(refresh: Boolean) =
        compositeDisposable.add(usersVlogsUseCase.getFeaturedVlogs(refresh)
            .doOnSubscribe { vlogs.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.map { pair -> Pair(pair.first, pair.second).mapToPresentation() } }
            .subscribe({ vlogs.setSuccess(it) }, { vlogs.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
