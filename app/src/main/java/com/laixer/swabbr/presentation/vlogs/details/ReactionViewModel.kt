package com.laixer.swabbr.presentation.vlogs.details

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.data.datasource.model.WatchReactionResponse
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.presentation.model.*
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
import kotlin.math.round
import kotlin.math.roundToInt

class ReactionViewModel constructor(
    private val mHttpClient: OkHttpClient,
    private val reactionsUseCase: UserReactionUseCase,
    private val context: Context
) : ViewModel() {

    val toast: Toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    val newReaction = MutableLiveData<Resource<UploadReactionItem>>()
    val watchReactionResponse = MutableLiveData<Resource<WatchReactionResponse>>()

    private val compositeDisposable = CompositeDisposable()

    fun watch(reactionId: UUID) = compositeDisposable.add(
        reactionsUseCase.watch(reactionId)
            .doOnSubscribe { watchReactionResponse.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { watchReactionResponse.setSuccess(it) },
                { watchReactionResponse.setError(it.message) }
            )
    )

    fun newReaction(targetVlogId: UUID) = compositeDisposable.add(
        reactionsUseCase.new(targetVlogId)
            .doOnSubscribe { newReaction.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { newReaction.setSuccess(it) },
                { newReaction.setError(it.message) }
            )
    )

    fun finishUploading(reactionId: UUID, onComplete: () -> Unit, onError: () -> Unit) = compositeDisposable.add(
        reactionsUseCase.finishUploading(reactionId)
            .doOnSubscribe { }
            .subscribeOn(Schedulers.io())
            .subscribe({ onComplete.invoke() }, { onError.invoke() })
    )

    fun uploadReaction(videoFileUri: Uri, uploadUrl: String) {
        context.contentResolver.openInputStream(videoFileUri)?.let {
            val bis = BufferedInputStream(it)
            val blockIds = emptyList<String>().toMutableList()

            var counter = 1
            val total = bis.available()
            while (bis.available() > 0) {
                val blockSize = 4 * 1024 * 1024 // 4 MB
                val bufferLength = if (bis.available() > blockSize) blockSize else bis.available()

                val buffer = ByteArray(bufferLength)
                bis.read(buffer, 0, buffer.size)

                val blockId = Base64.getEncoder().encodeToString(("Block-${counter++}").toByteArray(Charsets.UTF_8))

                val available = bis.available()
                viewModelScope.launch(Dispatchers.Main) {
                    toast.setText("Uploading chunk $counter/${
                        ceil(
                            total.toDouble().div(blockSize)
                        ).toInt() + 1
                    } (${(available.toDouble() / 1_000_000).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN)}MB remaining)")
                    toast.show()
                }

                uploadBlock(uploadUrl, buffer, blockId)
                blockIds.add(blockId)
            }

            commitBlockList(uploadUrl, blockIds)

            bis.close()
            it.close()
        }
    }

    private fun uploadBlock(baseUri: String, blockContents: ByteArray, blockId: String) {
        val mime = MediaType.get("video/mp4")
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
