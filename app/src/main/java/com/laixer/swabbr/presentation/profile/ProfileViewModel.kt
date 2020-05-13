package com.laixer.swabbr.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.FollowStatusItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class ProfileViewModel constructor(
    private val usersUseCase: UsersUseCase,
    private val userVlogsUseCase: UserVlogsUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {

    val profile = MutableLiveData<Resource<UserItem>>()
    val profileVlogs = MutableLiveData<Resource<List<VlogItem>>>()
    val followStatus = MutableLiveData<Resource<FollowStatusItem>>()
    private val compositeDisposable = CompositeDisposable()

    fun getProfile(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(usersUseCase
            .get(userId, refresh)
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { profile.setSuccess(it) },
                { profile.setError(it.message) }
            )
        )

    fun getProfileVlogs(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(userVlogsUseCase
            .get(userId, refresh)
            .doOnSubscribe { profileVlogs.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { profileVlogs.setSuccess(it) },
                { profileVlogs.setError(it.message) }
            )
        )

    fun getFollowStatus(userId: UUID) =
        compositeDisposable.add(followUseCase
            .getFollowStatus(userId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { followStatus.setSuccess(it.mapToPresentation()) },
                { followStatus.setError(it.message) }
            )
        )

    fun sendFollowRequest(userId: UUID) =
        compositeDisposable.add(followUseCase
            .sendFollowRequest(userId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { followStatus.setSuccess(it.status.mapToPresentation()) },
                { followStatus.setError(it.message) }
            )
        )

    fun unfollow(userId: UUID) =
        compositeDisposable.add(followUseCase
            .unfollow(userId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { followStatus.setSuccess(FollowStatusItem(FollowStatus.NOT_FOLLOWING)) },
                { followStatus.setError(it.message) }
            )
        )

    fun cancelFollowRequest(userId: UUID) =
        compositeDisposable.add(followUseCase
            .cancelFollowRequest(userId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { followStatus.setSuccess(FollowStatusItem(FollowStatus.NOT_FOLLOWING)) },
                { followStatus.setError(it.message) }
            )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
