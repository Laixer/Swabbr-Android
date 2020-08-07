package com.laixer.swabbr.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.fragment_auth_profile.*
import kotlinx.android.synthetic.main.fragment_profile.no_vlogs_text
import kotlinx.android.synthetic.main.fragment_profile.swipeRefreshLayout
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AuthProfileFragment : AuthFragment() {

    private val profileVm: ProfileViewModel by sharedViewModel()
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                profileVm.getProfileVlogs(
                    authenticatedUser.user.id,
                    refresh = true
                )
            }
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_auth_profile, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_userprofile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()
        updateProfile(authenticatedUser.user)

        profileVlogsAdapter = ProfileVlogsAdapter(requireContext(), onClick)

        profilevlogsRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter
        }

        profileVm.run {
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
            profile.observe(viewLifecycleOwner, Observer { updateProfile(it.data) })
            swipeRefreshLayout.setOnRefreshListener {
                getProfile(authenticatedUser.user.id, refresh = true)
                getProfileVlogs(authenticatedUser.user.id, refresh = true)
            }
        }

        if (savedInstanceState == null) {
            profileVm.run {
                getProfileVlogs(authenticatedUser.user.id, refresh = true)
                getProfile(authenticatedUser.user.id, refresh = true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_dest -> findNavController().navigate(AuthProfileFragmentDirections.actionViewSettings())
        }
        return super.onOptionsItemSelected(item)
    }

    private val onClick: (UserVlogItem) -> Unit = { item ->
        findNavController().navigate(Uri.parse("https://swabbr.com/user/${item.userId}/vlog/${item.vlogId}"))
    }

    private fun updateProfile(item: UserItem?) {
        item?.let { item ->
            userAvatar.loadAvatar(item.profileImage, item.id)
            nickname.text = requireContext().getString(R.string.nickname, item.nickname)
            item.firstName?.let {
                username.text = requireContext().getString(R.string.full_name, it, item.lastName)
                username.visibility = View.VISIBLE
            }
            vlog_count.text = requireContext().getString(R.string.vlog_count, item.totalVlogs)
        }
    }

    private fun updateProfileVlogs(res: Resource<List<UserVlogItem>?>) = res.run {
        with(swipeRefreshLayout) {
            when (state) {
                ResourceState.LOADING -> startRefreshing()
                ResourceState.SUCCESS -> {
                    stopRefreshing()
                    data?.let {
                        no_vlogs_text.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                        profileVlogsAdapter?.submitList(it)
                    }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                    message?.let { snackBar.show() }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }
}
