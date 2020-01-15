package com.laixer.swabbr.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel constructor(
    private val usersUseCase: UsersUseCase,
    private val userVlogsUseCase: UserVlogsUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {

    val profile = MutableLiveData<ProfileItem?>()
    val profileVlogs = MutableLiveData<Resource<List<VlogItem>>>()
    val followStatus = MutableLiveData<Resource<Int>>()
    private val compositeDisposable = CompositeDisposable()

    fun getProfile(userId: String, refresh: Boolean = false) =
        compositeDisposable.add(
            usersUseCase.get(userId, refresh)
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe({ profile.postValue(it) }, { })
        )

    fun getProfileVlogs(userId: String, refresh: Boolean = false) =
        compositeDisposable.add(
            userVlogsUseCase.get(userId, refresh)
                .doOnSubscribe { profileVlogs.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe({ profileVlogs.setSuccess(it) }, { profileVlogs.setError(it.message) })
        )

    fun getFollowStatus(userId: String) =
        compositeDisposable.add(
            followUseCase.getFollowStatus(userId)
                .doOnSubscribe { followStatus.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe({ followStatus.setSuccess(it) }, { followStatus.setError(it.message) })
        )

    fun sendFollowRequest(userId: String) =
        compositeDisposable.add(
            followUseCase.sendFollowRequest(userId)
                .doOnSubscribe { followStatus.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe({ followStatus.setSuccess(it) }, { followStatus.setError(it.message) })
        )

    fun unfollow(userId: String) =
        compositeDisposable.add(
            followUseCase.unfollow(userId)
                .doOnSubscribe { followStatus.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe({ followStatus.setSuccess(it) }, { followStatus.setError(it.message) })
        )

    fun cancelFollowRequest(userId: String) =
        compositeDisposable.add(
            followUseCase.cancelFollowRequest(userId)
                .doOnSubscribe { followStatus.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe({ followStatus.setSuccess(it) }, { followStatus.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
