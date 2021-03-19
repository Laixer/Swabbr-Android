package com.laixer.swabbr.presentation.reaction.playback

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.utils.todosortme.setError
import com.laixer.swabbr.presentation.utils.todosortme.setLoading
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.presentation.abstraction.ViewModelBase
import com.laixer.swabbr.utils.resources.Resource
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model containing functionality for watching reactions.
 */
class ReactionViewModel constructor(
    private val reactionsUseCase: ReactionUseCase
) : ViewModelBase() {
    /**
     *  Resource in which [getReaction] stores its result.
     */
    val reaction = MutableLiveData<Resource<ReactionWrapperItem>>()

    /**
     *  Gets a reaction from the data store and stores it in
     *  [reaction] on completion.
     */
    fun getReaction(reactionId: UUID) = compositeDisposable.add(
        reactionsUseCase.get(reactionId)
            .doOnSubscribe { reaction.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { reaction.setSuccess(it.mapToPresentation()) },
                {
                    reaction.setError(it.message)
                    Log.e(TAG, "Could not get reaction $reactionId. Message: ${it.message}")
                }
            )
    )

    companion object {
        private val TAG = ReactionViewModel::class.java.simpleName
    }
}
