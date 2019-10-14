package com.laixer.sample.presentation.vloglist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.sample.presentation.model.mapToPresentation
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.sample.domain.usecase.UsersVlogsUseCase
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.VlogItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VlogListViewModel constructor(private val usersVlogsUseCase: UsersVlogsUseCase) :
    ViewModel() {

    val vlogs = MutableLiveData<Resource<List<Pair<ProfileItem, VlogItem>>>>()
    private val compositeDisposable = CompositeDisposable()

    fun get(refresh: Boolean = false) =
        compositeDisposable.add(usersVlogsUseCase.get(refresh)
            .doOnSubscribe { vlogs.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.map { pair ->  Pair(pair.first.mapToPresentation(), pair.second.mapToPresentation() )} }
            .subscribe({ vlogs.setSuccess(it) }, { vlogs.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
