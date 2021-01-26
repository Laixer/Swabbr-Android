package com.laixer.swabbr.presentation.vlogs.details

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
 *  Viewmodel containing functionality for watching and posting
 *  reactions. This includes uploading functionality.
 */
class ReactionViewModel constructor(
    private val mHttpClient: OkHttpClient,
    private val reactionsUseCase: ReactionUseCase,
    private val context: Context
) : ViewModel() {
    val toast: Toast = Toast.makeText(context, "", Toast.LENGTH_LONG)

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

    /**
     *  Uploads a file to the blob storage.
     *
     *  Note that the id of the reaction should already be contained
     *  in the name of the video file.
     *
     *  @param localVideoUri The local uri to the recorded video file.
     *  @param uploadUrl The uri to which the video file should be uploaded.
     *  @param contentType The MIME type.
     */
    private fun uploadFile(localVideoUri: Uri, uploadUrl: Uri, contentType: String) {
        context.contentResolver.openInputStream(localVideoUri)?.let {
            val bis = BufferedInputStream(it)
            val blockIds = emptyList<String>().toMutableList()

            var counter = 1
            val total = bis.available()
            while (bis.available() > 0) {
                val blockSize = 4 * 1024 * 1024 // 4 MB
                val bufferLength = if (bis.available() > blockSize) blockSize else bis.available()

                val buffer = ByteArray(bufferLength)
                bis.read(buffer, 0, buffer.size)

                val blockId =
                    Base64.getEncoder().encodeToString(("Block-${counter++}").toByteArray(Charsets.UTF_8))

                val available = bis.available()
                viewModelScope.launch(Dispatchers.Main) {
                    toast.setText(
                        "Uploading chunk $counter/${
                            ceil(
                                total.toDouble().div(blockSize)
                            ).toInt() + 1
                        } (${
                            (available.toDouble() / 1_000_000).toBigDecimal()
                                .setScale(1, RoundingMode.HALF_EVEN)
                        }MB remaining)"
                    )
                    toast.show()
                }

                uploadBlock(uploadUrl.toString(), buffer, blockId, contentType)
                blockIds.add(blockId)
            }

            commitBlockList(uploadUrl.toString(), blockIds)

            bis.close()
            it.close()
        }
    }

    /**
     *  Upload a block of bytes.
     *
     *  @param baseUri The uri to upload to.
     *  @param blockContents The contents to upload.
     *  @param blockId The id of this block.
     *  @param contentType MIME type.
     */
    private fun uploadBlock(
        baseUri: String,
        blockContents: ByteArray,
        blockId: String,
        contentType: String
    ) {
        val mime = MediaType.get(contentType) // TODO Not that bulletproof, but private so ok for now
        val body = RequestBody.create(mime, blockContents)

        Log.d(TAG, body.toString())
        val uploadBlockUri = "$baseUri&comp=block&blockId=$blockId"
        val request = Request.Builder()
            .url(uploadBlockUri)
            .put(body)
            .addHeader("x-ms-version", X_MS_VERSION)
            .addHeader("x-ms-blob-type", X_MS_BLOB_TYPE)
            .addHeader("No-Authentication", true.toString())
            .build()

        mHttpClient.newCall(request).execute()
    }

    private fun commitBlockList(baseUri: String, blockIds: List<String>) {
        val blockIdsPayload = StringBuilder().apply {
            append("<?xml version='1.0' ?><BlockList>")
            for (blockId in blockIds) {
                append("<Latest>$blockId</Latest>")
            }
            append("</BlockList>")
        }

        Log.d(TAG, blockIdsPayload.toString())

        val putBlockListUrl = "$baseUri&comp=blockList"
        val contentType = MediaType.get("video/mp4")
        val body = RequestBody.create(contentType, blockIdsPayload.toString())

        val request = Request.Builder()
            .url(putBlockListUrl)
            .put(body)
            .addHeader("x-ms-version", X_MS_VERSION)
            .addHeader("No-Authentication", true.toString())
            .build()

        mHttpClient.newCall(request).execute()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    companion object {
        private const val TAG = "ReactionViewModel"
        private const val X_MS_VERSION = "2019-02-02"
        private const val X_MS_BLOB_TYPE = "BlockBlob"
    }
}
