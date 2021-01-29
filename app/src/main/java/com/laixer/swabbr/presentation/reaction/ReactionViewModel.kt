package com.laixer.swabbr.presentation.reaction

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.recording.UploadVideoViewModel
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.BufferedInputStream
import java.math.RoundingMode
import java.util.*
import kotlin.math.ceil

/**
 *  View model containing functionality for watching and posting
 *  reactions. This includes uploading functionality.
 */
class ReactionViewModel constructor(
    private val mHttpClient: OkHttpClient,
    private val reactionsUseCase: ReactionUseCase,
    private val context: Context
) : UploadVideoViewModel(mHttpClient, context) {
    /**
     *  Mutable resource in which a reaction we want to watch will be
     *  stored dynamically.
     */
    val watchReactionResponse = MutableLiveData<Resource<ReactionWrapperItem>>()

    private val compositeDisposable = CompositeDisposable()

    /**
     *  Gets a reaction from the data store and stores it in
     *  [watchReactionResponse] on completion.
     */
    fun watch(reactionId: UUID) = compositeDisposable.add(
        reactionsUseCase.get(reactionId)
            .doOnSubscribe { watchReactionResponse.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { watchReactionResponse.setSuccess(it.mapToPresentation()) },
                { watchReactionResponse.setError(it.message) }
            )
    )

    // TODO This is messy.
    // TODO Hard coded content types
    // TODO Make sure the order of execution is correct! It works though...
    // TODO This error hides
    /**
     *  Uploads a [ReactionItem] including thumbnail and posts the
     *  reaction to the backend.
     *
     *  @param localVideoUri Location of the reaction video file.
     *  @param localThumbnailUri Location of the thumbnail file.
     *  @param targetVlogId The vlog to post a reaction to.
     *  @param isPrivate Indicates reaction accessibility.
     */
    fun postReaction(
        localVideoUri: Uri,
        localThumbnailUri: Uri,
        targetVlogId: UUID,
        isPrivate: Boolean
    ): Completable =
        reactionsUseCase.generateUploadWrapper()
            .map { uploadWrapper ->
                Completable.fromCallable {
                    uploadFile(localVideoUri, uploadWrapper.videoUploadUri, "video/mp4")
                    uploadFile(localThumbnailUri, uploadWrapper.thumbnailUploadUri, "image/jpeg")
                }
                    .andThen(
                        reactionsUseCase.postReaction(
                            ReactionItem.createForPosting(
                                id = uploadWrapper.id,
                                targetVlogId = targetVlogId,
                                isPrivate = isPrivate
                            ).mapToDomain()
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            .ignoreElement()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    companion object {
        private const val TAG = "ReactionViewModel"
    }
}
