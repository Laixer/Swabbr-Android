package com.laixer.swabbr.presentation.profile

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.utils.todosortme.startRefreshing
import com.laixer.swabbr.presentation.utils.todosortme.stopRefreshing
import com.laixer.swabbr.presentation.vlogs.list.VlogListCardAdapter
import com.laixer.swabbr.utils.media.MediaConstants
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_profile_vlogs.*
import java.util.*

/**
 *  Fragment displaying the vlogs of the currently authenticated
 *  user. This should be inflated as a tab in [ProfileFragment].
 *
 *  @param userId The user id of the profile we are looking at.
 *  @param profileVm Single profile vm instance from [ProfileFragment].
 */
class ProfileVlogsFragment(
    private val userId: UUID,
    private val profileVm: ProfileViewModel
) : AuthFragment() {

    /** Adapter for [recycler_view_profile_vlogs] - NOT the fullscreen playback adapter. */
    private var profileVlogsAdapter: VlogListCardAdapter? = null

    /**
     *  Inflate the view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_vlogs, container, false)

    /**
     *  Bind UI and start the data fetch.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVm.userVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })

        profileVlogsAdapter = VlogListCardAdapter(
            selfId = getSelfId(),
            onClickVlog = onClickVlog,
            onClickDelete = onClickDeleteVlog,
            onClickShare = onClickShareVlog
        )

        //recycler_view_profile_vlogs.isNestedScrollingEnabled = false
        recycler_view_profile_vlogs.adapter = profileVlogsAdapter

        swipe_refresh_layout_profile_vlogs.setOnRefreshListener { refreshData() }

        // Set the empty collection text based on who we are looking at
        text_view_profile_vlogs_none.text = if (userId == getSelfId())
            requireContext().getString(R.string.profile_self_no_vlogs)
        else requireContext().getString(R.string.profile_no_vlogs)
    }

    /**
     *  Only performs data refreshes. Note that this does not
     *  follow the [getData] structure as our parent fragment
     *  manages the initial data get operation.
     */
    private fun refreshData() {
        profileVm.getVlogsByUser(userId, true)
    }

    /**
     *  Called when we click on a vlog item in the [profileVlogsAdapter].
     */
    private val onClickVlog: (VlogWrapperItem) -> Unit = { item ->
        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToWatchUserVlogsFragment(
                initialVlogId = item.vlog.id.toString(),
                userId = item.user.id.toString()
            )
        )
    }

    /**
     *  Called when we click the delete icon for a vlog. Note that this
     *  only works for vlogs owned by the current user.
     */
    private val onClickDeleteVlog: (VlogWrapperItem) -> Unit = { item ->
        profileVm.deleteVlog(item.vlog.mapToDomain()) // TODO Mapping here? Shouldn't be necessary.
    }

    /**
     *  Called when we click the share icon for a vlog. Note that this
     *  only works for vlogs owned by the current user.
     */
    private val onClickShareVlog: (VlogWrapperItem) -> Unit = { item ->

        // TODO Check if we don't already have the file stored.

        val dm = requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(item.vlog.videoUri)
        request.setAllowedOverRoaming(false)
        request.setTitle("Your Swabbr Vlog")
        request.setDescription("Downloading the video file so you can share it")

        val referenceDownloadId = dm.enqueue(request)

        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.getLongExtra("extra_download_id", -1)?.let { downloadId ->

                    // If our download ids match we can share.
                    if (downloadId == referenceDownloadId) {
                        val fileUri = dm.getUriForDownloadedFile(downloadId)
                        val fileMimeType = dm.getMimeTypeForDownloadedFile(downloadId)

                        val shareIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            type = fileMimeType
                            putExtra(Intent.EXTRA_TITLE, "Sharing your Swabbr vlog")
                        }

                        // Attempt to launch the chooser intent.
                        try {
                            startActivity(
                                Intent.createChooser(
                                    shareIntent, resources.getText(R.string.chooser_share_title)
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Could not start share intent", e)
                            return
                        }
                    }
                }
            }
        }

        requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    /**
     *  Called when the observed user [VlogWrapperItem] resource
     *  list changes.
     */
    private fun updateProfileVlogs(res: Resource<List<VlogWrapperItem>>) = res.run {
        when (state) {
            ResourceState.LOADING -> {
                swipe_refresh_layout_profile_vlogs.startRefreshing()
            }
            ResourceState.SUCCESS -> {
                swipe_refresh_layout_profile_vlogs.stopRefreshing()

                data?.let {
                    text_view_profile_vlogs_none.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE

                    profileVlogsAdapter?.submitList(it)
                    profileVlogsAdapter?.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                swipe_refresh_layout_profile_vlogs.stopRefreshing()

                showMessage("Error getting user vlogs")
            }
        }
    }

    /**
     *  Disposes our resources.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
