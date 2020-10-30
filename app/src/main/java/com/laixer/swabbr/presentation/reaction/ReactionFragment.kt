package com.laixer.swabbr.presentation.reaction

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UploadReactionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

open class ReactionFragment : AuthFragment() {

    private val args: ReactionFragmentArgs by navArgs()
    private val vm: ReactionViewModel by inject()
    private val mHttpClient: OkHttpClient by inject()
    private lateinit var videoFileUri: Uri
    private lateinit var uploadUrl: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        injectFeature()

        vm.newReaction.observe(viewLifecycleOwner, Observer { observeNewReaction(it) })
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.newReaction(UUID.fromString(args.vlogId))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            intent?.data?.let {
                videoFileUri = it

//                    val arr = it.toString().split("?sv=")
//                videoFileUri = Uri("${arr[0]}/video.mp4${arr[1]}"

                    // Upload to Azure
                lifecycleScope.launch(Dispatchers.IO) {
                    uploadToSAS()

                    vm.finishUploading(vm.newReaction.value!!.data!!.reaction.id, {
                        Toast.makeText(requireContext(), "Finished uploading reaction!", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }, {
                        Toast.makeText(requireContext(), "Failed to upload reaction.", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    })
                }

            }

        }
    }

    private fun observeNewReaction(resource: Resource<UploadReactionItem>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                // Present loading state

            }
            ResourceState.SUCCESS -> {
                data?.let {
                    val arr = it.uploadUrl.split("?sv=")
                    uploadUrl = "${arr[0]}/video.mp4?sv=${arr[1]}"
                    Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                        takeVideoIntent.putExtras(bundleOf(MediaStore.EXTRA_DURATION_LIMIT to DEFAULT_MAXIMUM_RECORD_TIME_SECONDS))
                        takeVideoIntent.resolveActivity(requireContext().packageManager)?.also {
                            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                        }
                    }
                }
            }
            ResourceState.ERROR -> {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun uploadToSAS() {
        requireContext().contentResolver.openInputStream(videoFileUri)?.let {
            val bis = BufferedInputStream(it)
            val blockIds = emptyList<String>().toMutableList()

            var counter = 1
            while (bis.available() > 0) {
                val blockSize = 4 * 1024 * 1024 // 4 MB
                val bufferLength = if (bis.available() > blockSize) blockSize else bis.available()

                val buffer = ByteArray(bufferLength)
                bis.read(buffer, 0, buffer.size)

                val blockId = Base64.getEncoder().encodeToString(("Block-${counter++}").toByteArray(Charsets.UTF_8))
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

    companion object {

        private const val TAG = "ReactionFragment"
        private const val REQUEST_VIDEO_CAPTURE = 1
        private const val DEFAULT_MAXIMUM_RECORD_TIME_SECONDS = 10


        private const val X_MS_VERSION = "2019-02-02"
        private const val X_MS_BLOB_TYPE = "BlockBlob"

        /** Creates a [File] named with the current date and time */
        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
        }
    }
}
