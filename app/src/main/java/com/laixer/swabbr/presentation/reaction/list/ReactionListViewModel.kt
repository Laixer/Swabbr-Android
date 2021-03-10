package com.laixer.swabbr.presentation.reaction.list

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.recording.UploadVideoViewModel
import com.laixer.swabbr.presentation.viewmodel.ViewModelBase
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.util.*

/**
 *  View model containing functionality for watching a list
 *  of reactions that belong to a vlog.
 */
class ReactionListViewModel constructor(
    private val reactionsUseCase: ReactionUseCase
) : ViewModelBase() {
    /**
     *  Resource in which [getReactionsForVlog] stores its result.
     */
    val reactions = MutableLiveData<Resource<List<ReactionWrapperItem>>>()

    /**
     *  Gets a reactions for a vlog from the data store and stores
     *  them in [reactions] on completion.
     */
    fun getReactionsForVlog(vlogId: UUID, refresh: Boolean = false) = compositeDisposable.add(
        reactionsUseCase.getAllForVlog(vlogId, refresh)
            .doOnSubscribe { reactions.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { reactions.setSuccess(it.mapToPresentation()) },
                { reactions.setError(it.message) }
            )
    )

    companion object {
        private const val TAG = "ReactionViewModel"
    }
}
