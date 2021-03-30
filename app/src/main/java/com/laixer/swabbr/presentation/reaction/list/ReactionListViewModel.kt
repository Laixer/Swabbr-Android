package com.laixer.swabbr.presentation.reaction.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.presentation.abstraction.ViewModelBase
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.utils.todosortme.setError
import com.laixer.swabbr.presentation.utils.todosortme.setLoading
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.utils.resources.Resource
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
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
    fun getReactionsForVlog(vlogId: UUID, refresh: Boolean = false) =
        viewModelScope.launch {
            compositeDisposable.add(
                reactionsUseCase.getAllForVlog(vlogId, refresh)
                    .doOnSubscribe { reactions.setLoading() }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { reactions.setSuccess(it.mapToPresentation()) },
                        {
                            reactions.setError(it.message)
                            Log.e(TAG, "Error while getting reactions for vlog: ${it.message}")
                        }
                    )
            )
        }

    companion object {
        private const val TAG = "ReactionViewModel"
    }
}
