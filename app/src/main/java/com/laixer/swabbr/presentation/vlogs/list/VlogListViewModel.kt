package com.laixer.swabbr.presentation.vlogs.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.domain.usecase.VlogsUseCase
import com.laixer.swabbr.presentation.model.LikeListItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class VlogListViewModel constructor(
    private val usersVlogsUseCase: UsersVlogsUseCase,
    private val vlogsUseCase: VlogsUseCase
) : ViewModel() {

    val vlogs = MutableLiveData<Resource<List<UserVlogItem>>>()
    val likes = MutableLiveData<Resource<LikeListItem>>()
    private val compositeDisposable = CompositeDisposable()

    fun getRecommendedVlogs(refresh: Boolean) =
        compositeDisposable.add(
            usersVlogsUseCase.getRecommendedVlogs(refresh)
                .doOnSubscribe { vlogs.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { list ->
                    list.map { pair -> Pair(pair.first, pair.second) }
                        .sortedByDescending { it.second.data.dateStarted }
                        .mapToPresentation()
                }
                .subscribe(
                    { vlogs.setSuccess(it) },
                    { vlogs.setError(it.message) }
                )
        )

    fun getLikes(vlogId: UUID) =
        compositeDisposable.add(
            vlogsUseCase.getLikes(vlogId)
                .doOnSubscribe { likes.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe(
                    { likes.setSuccess(it) },
                    { likes.setError(it.message) }
                )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
