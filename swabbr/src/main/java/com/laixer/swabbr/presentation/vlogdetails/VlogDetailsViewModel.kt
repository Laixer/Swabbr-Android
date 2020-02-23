package com.laixer.swabbr.presentation.vlogdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.presentation.model.ProfileVlogItem
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VlogDetailsViewModel constructor(
    private val usersVlogsUseCase: UsersVlogsUseCase,
    private val reactionsUseCase: UserReactionUseCase
) : ViewModel() {

    val vlogs = MutableLiveData<Resource<List<ProfileVlogItem>>>()
    val reactions = MutableLiveData<Resource<List<ReactionItem>>>()
    private val compositeDisposable = CompositeDisposable()

    fun getVlogs(ids: Array<String>, refresh: Boolean = false) =
        compositeDisposable.add(usersVlogsUseCase.get(ids, refresh)
            .doOnSubscribe { vlogs.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe({ vlogs.setSuccess(it) }, { vlogs.setError(it.message) })
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
