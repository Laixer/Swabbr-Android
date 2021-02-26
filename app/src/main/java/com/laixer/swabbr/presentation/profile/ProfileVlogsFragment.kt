package com.laixer.swabbr.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.vlogs.list.VlogListCardAdapter
import kotlinx.android.synthetic.main.fragment_profile_vlogs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 *  Fragment displaying the vlogs of the currently authenticated
 *  user. This should be inflated as a tab in [ProfileFragment].
 *
 *  @param userId The user id of the profile we are looking at.
 */
class ProfileVlogsFragment(private val userId: UUID) : AuthFragment() {
    private val profileVm: ProfileViewModel by viewModel()

    /** Adapter for [recycler_view_profile_vlogs] - NOT the fullscreen playback adapter. */
    private var profileVlogsAdapter: VlogListCardAdapter? = null

    /**
     *  Inflate the view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_vlogs, container, false)
    }

    /**
     *  Bind UI and start the data fetch.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVm.userVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })

        profileVlogsAdapter = VlogListCardAdapter(
            selfId = getSelfId(),
            onClickVlog = onClickVlog,
            onClickDelete = onClickDeleteVlog
        )

        //recycler_view_profile_vlogs.isNestedScrollingEnabled = false
        recycler_view_profile_vlogs.adapter = profileVlogsAdapter

        swipe_refresh_layout_profile_vlogs.setOnRefreshListener { getData(true) }

        // Set the empty collection text based on who we are looking at
        text_view_profile_vlogs_none.text = if (userId == getSelfId())
            requireContext().getString(R.string.profile_self_no_vlogs)
        else requireContext().getString(R.string.profile_no_vlogs)

        // Get the data right away
        getData(false)
    }

    /**
     *  Gets the vlogs from the [profileVm].
     *
     *  @param refresh Force a data refresh.
     */
    private fun getData(refresh: Boolean = false) {
        profileVm.getVlogsByUser(userId, refresh)
    }

    /**
     *  Called when we click on a vlog item in the [profileVlogsAdapter].
     */
    private val onClickVlog: (VlogWrapperItem) -> Unit = { item ->
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToWatchUserVlogsFragment(
            initialVlogId = item.vlog.id.toString(),
            userId = item.user.id.toString()
        ))
    }

    /**
     *  Called when we click the delete icon for a vlog. Note that this
     *  only works for vlogs owned by the current user.
     */
    private val onClickDeleteVlog: (VlogWrapperItem) -> Unit = { item ->
        profileVm.deleteVlog(item.vlog.mapToDomain()) // TODO Mapping here? Shouldn't be necessary.
    }

    /**
     *  Called when the observed user [VlogWrapperItem] resource
     *  list changes.
     */
    private fun updateProfileVlogs(res: Resource<List<VlogWrapperItem>>) = res.run {
        when (state) {
            ResourceState.LOADING -> swipe_refresh_layout_profile_vlogs.startRefreshing()
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
