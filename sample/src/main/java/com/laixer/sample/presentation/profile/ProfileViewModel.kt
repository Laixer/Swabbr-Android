package com.laixer.sample.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.sample.domain.usecase.FollowUseCase
import com.laixer.sample.domain.usecase.UserVlogsUseCase
import com.laixer.sample.domain.usecase.UsersUseCase
import com.laixer.sample.presentation.model.FollowRequestItem
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.VlogItem
import com.laixer.sample.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel constructor(
    private val usersUseCase: UsersUseCase,
    private val userVlogsUseCase: UserVlogsUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {

    val profile = MutableLiveData<ProfileItem?>()
    val profileVlogs = MutableLiveData<Resource<List<VlogItem>>>()
    val followRequest = MutableLiveData<FollowRequestItem?>()
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
                .map { it.second.mapToPresentation() }
                .subscribe({ profileVlogs.setSuccess(it) }, { profileVlogs.setError(it.message) })
        )

    fun getFollowRequest(receiverId: String) =
        compositeDisposable.add(
            followUseCase.get(receiverId)
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe({ followRequest.postValue(it) }, { })
        )

    fun requestFollow(receiverId: String) {
        followUseCase.requestFollow(receiverId)
    }

    fun requestUnfollow(receiverId: String) {
        followUseCase.requestUnfollow(receiverId)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
