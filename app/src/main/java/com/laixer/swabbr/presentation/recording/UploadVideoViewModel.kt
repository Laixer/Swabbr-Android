package com.laixer.swabbr.presentation.recording

import android.content.Context
import android.net.Uri
import android.util.Log
import com.laixer.swabbr.presentation.abstraction.ViewModelBase
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.BufferedInputStream
import java.math.RoundingMode
import java.util.*
import kotlin.math.ceil

// TODO Refactor this by a lot.
/**
 *  View model containing functionality for uploading a video.
 */
open class UploadVideoViewModel constructor(
    private val mHttpClient: OkHttpClient
) : ViewModelBase() {
    // TODO Move upload functionality to a helper.
    /**
     *  Uploads a file to an external uri.
     *
     *  @param localVideoUri The local uri to the recorded video file.
     *  @param uploadUri The uri to which the video file should be uploaded.
     *  @param contentTypeString The MIME type.
     */
    protected fun uploadFile(context: Context, localVideoUri: Uri, uploadUri: Uri, contentTypeString: String) {
        context.contentResolver.openInputStream(localVideoUri)?.let {
            val bis = BufferedInputStream(it)
            val blockIds = emptyList<String>().toMutableList()

            val contentType = MediaType.get(contentTypeString)

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
                Log.d(
                    TAG,
                    "Uploading chunk $counter/${
                        ceil(
                            total.toDouble().div(blockSize)
                        ).toInt() + 1
                    } (${
                        (available.toDouble() / 1_000_000).toBigDecimal()
                            .setScale(1, RoundingMode.HALF_EVEN)
                    }MB remaining)"
                )

                uploadBlock(uploadUri.toString(), buffer, blockId, contentType)
                blockIds.add(blockId)
            }

            commitBlockList(uploadUri.toString(), contentType, blockIds)

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
        contentType: MediaType
    ) {
        val body = RequestBody.create(contentType, blockContents)

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

    private fun commitBlockList(baseUri: String, contentType: MediaType, blockIds: List<String>) {
        val blockIdsPayload = StringBuilder().apply {
            append("<?xml version='1.0' ?><BlockList>")
            for (blockId in blockIds) {
                append("<Latest>$blockId</Latest>")
            }
            append("</BlockList>")
        }

        Log.d(TAG, blockIdsPayload.toString())

        val putBlockListUrl = "$baseUri&comp=blockList"
        val body = RequestBody.create(contentType, blockIdsPayload.toString())

        val request = Request.Builder()
            .url(putBlockListUrl)
            .put(body)
            .addHeader("x-ms-version", X_MS_VERSION)
            .addHeader("No-Authentication", true.toString())
            .build()

        mHttpClient.newCall(request).execute()
    }

    companion object {
        private val TAG = UploadVideoViewModel::class.java.simpleName
        private const val X_MS_VERSION = "2019-02-02"
        private const val X_MS_BLOB_TYPE = "BlockBlob"
    }
}
