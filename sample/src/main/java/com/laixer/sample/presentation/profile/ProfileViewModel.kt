package com.laixer.sample.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.sample.domain.usecase.UserVlogsUseCase
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel constructor(
    private val userVlogsUseCase: UserVlogsUseCase
) : ViewModel() {

    val profile = MutableLiveData<ProfileItem>()
    private val compositeDisposable = CompositeDisposable()

    fun get(userId: String) =
        compositeDisposable.add(
            userVlogsUseCase.get(userId, false)
                .subscribeOn(Schedulers.io())
                .subscribe({ profile.postValue(it.mapToPresentation()) }, { })
        )


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
