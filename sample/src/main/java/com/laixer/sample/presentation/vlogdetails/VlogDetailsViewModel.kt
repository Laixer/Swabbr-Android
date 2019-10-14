package com.laixer.sample.presentation.vlogdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.sample.presentation.model.mapToPresentation
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.sample.domain.usecase.UserReactionUseCase
import com.laixer.sample.domain.usecase.UserVlogUseCase
import com.laixer.sample.presentation.model.ProfileVlogItem
import com.laixer.sample.presentation.model.ReactionItem
import com.laixer.sample.presentation.model.VlogItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VlogDetailsViewModel constructor(
    private val userVlogUseCase: UserVlogUseCase,
    private val reactionsUseCase: UserReactionUseCase
) : ViewModel() {

    val vlog = MutableLiveData<ProfileVlogItem>()
    val reactions = MutableLiveData<Resource<List<ReactionItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun getVlog(vlogId: String) =
        compositeDisposable.add(userVlogUseCase.get(vlogId, false)
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe({ vlog.postValue(it) }, { })
        )

    fun getReactions(vlogId: String, refresh: Boolean = false) =
        compositeDisposable.add(reactionsUseCase.get(vlogId, refresh)
            .doOnSubscribe { reactions.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe({ reactions.setSuccess(it) }, { reactions.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
