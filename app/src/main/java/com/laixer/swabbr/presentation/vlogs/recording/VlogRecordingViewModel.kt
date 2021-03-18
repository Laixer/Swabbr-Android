package com.laixer.swabbr.presentation.vlogs.recording

import android.content.Context
import android.net.Uri
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.recording.UploadVideoViewModel
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.util.*

/**
 *  View model containing functionality for recording vlogs.
 *  This includes uploading functionality.
 */
class VlogRecordingViewModel constructor(
    mHttpClient: OkHttpClient,
    private val vlogUseCase: VlogUseCase,
    context: Context
) : UploadVideoViewModel(mHttpClient, context) {
    // TODO This is messy.
    // TODO Hard coded content types
    // TODO Make sure the order of execution is correct! It works though...
    // TODO This error hides
    /**
     *  Uploads a [VlogItem] including thumbnail and posts the
     *  vlog to the backend.
     *
     *  @param localVideoUri Location of the vlog video file.
     *  @param localThumbnailUri Location of the thumbnail file.
     *  @param isPrivate Indicates reaction accessibility.
     */
    fun postVlog(
        localVideoUri: Uri,
        localThumbnailUri: Uri,
        isPrivate: Boolean
    ): Completable =
        vlogUseCase.generateUploadWrapper()
            .map { uploadWrapper ->
                Completable.fromCallable {
                    uploadFile(localVideoUri, uploadWrapper.videoUploadUri, "video/mp4")
                    uploadFile(localThumbnailUri, uploadWrapper.thumbnailUploadUri, "image/jpeg")
                }
                    .andThen(
                        vlogUseCase.postVlog(
                            VlogItem.createForPosting(
                                id = uploadWrapper.id,
                                isPrivate = isPrivate
                            ).mapToDomain()
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .subscribe({}, {}) // We always want an error handler even if it's empty.
            }
            .ignoreElement()

    companion object {
        private const val TAG = "ReactionViewModel"
    }
}
