package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.UnauthenticatedException
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.fragment_auth_profile.*
import kotlinx.android.synthetic.main.fragment_profile.no_vlogs_text
import kotlinx.android.synthetic.main.fragment_profile.profilevlogsRecyclerView
import kotlinx.android.synthetic.main.fragment_profile.swipeRefreshLayout
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class AuthProfileFragment : Fragment() {

    private val profileVm: ProfileViewModel by viewModel()
    private val authVm: AuthViewModel by viewModel()
    private var userId: UUID? = null
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { userId?.let { profileVm.getProfileVlogs(it, refresh = true) } }
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        authVm.run {
            authenticatedUser.observe(viewLifecycleOwner, Observer { res ->
                with(res) {
                    when (state) {
                        ResourceState.LOADING -> {
                            return@Observer
                        }
                        ResourceState.SUCCESS -> res.data?.let {
                            userId = it.user.id
                            setup(it.user, savedInstanceState)
                        }
                        ResourceState.ERROR -> throw UnauthenticatedException("User is not authenticated")
                    }
                }
            })
            get()
        }
    }

    private fun setup(user: UserItem, savedInstanceState: Bundle?) {
        profileVlogsAdapter = ProfileVlogsAdapter(requireContext(), onClick)
        if (savedInstanceState == null) {
            profileVm.run {
                with(user.id) {
                    getProfileVlogs(this)
                }
            }
        }

        profilevlogsRecyclerView.run {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter
        }

        profileVm.run {
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
            swipeRefreshLayout.setOnRefreshListener {
                getProfileVlogs(user.id, refresh = true)
            }
        }
        updateProfile(user)
    }

    private val onClick: (VlogItem) -> Unit = {
        findNavController().navigate(
            AuthProfileFragmentDirections.actionViewVlog(
                arrayOf(it.id.toString())
            )
        )
    }

    private fun updateProfile(userItem: UserItem?) {
        userItem?.let {
            userAvatar.loadAvatar(it.profileImageUrl)
            userUsername.text = requireContext().getString(R.string.nickname, it.nickname)
            userName.text = requireContext().getString(R.string.full_name, it.firstName, it.lastName)
            vlog_count.text = requireContext().getString(R.string.vlog_count, it.totalVlogs)
        }
    }

    private fun updateProfileVlogs(res: Resource<List<VlogItem>?>) = res.run {
        with(swipeRefreshLayout) {
            when (state) {
                ResourceState.LOADING -> startRefreshing()
                ResourceState.SUCCESS -> stopRefreshing()
                ResourceState.ERROR -> stopRefreshing()
            }
        }

        data?.let {
            no_vlogs_text.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            profileVlogsAdapter?.submitList(it)
        }
        message?.let { snackBar.show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }
}
