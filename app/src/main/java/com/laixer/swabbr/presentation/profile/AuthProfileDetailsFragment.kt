package com.laixer.swabbr.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.*
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
import kotlinx.android.synthetic.main.fragment_auth_profile_details.*
import kotlinx.android.synthetic.main.fragment_profile.no_vlogs_text
import kotlinx.android.synthetic.main.fragment_profile.swipeRefreshLayout
import kotlinx.android.synthetic.main.include_user_details.*
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AuthProfileDetailsFragment : AuthFragment() {

    private val profileVm: ProfileViewModel by sharedViewModel()
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                authUserVm.getAuthUserId()?.let {
                    profileVm.getProfileVlogs(
                        it,
                        refresh = true
                    )
                }

            }
            .setDuration(Snackbar.LENGTH_LONG)
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVm.run {
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
        }

        profileVlogsAdapter = ProfileVlogsAdapter(requireContext(), profileVm, authUserVm, onClick, onDelete)

        profilevlogsRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter  // MAKE SURE THIS HAPPENS BEFORE ADAPTER INSTANTIATION
        }

        authUserVm.getAuthUserId()?.let {
            profileVm.run {
                swipeRefreshLayout.setOnRefreshListener {
                    getProfileVlogs(it, refresh = true)
                }

                getProfileVlogs(it, refresh = true)
            }
        }
    }

    private val onClick: (UserVlogItem) -> Unit = { item ->
        findNavController().navigate(Uri.parse("https://swabbr.com/profileWatchVlog?userId=${item.user.id}&vlogId=${item.vlog.data.id}"))
    }

    private val onDelete: (UserVlogItem) -> Unit = { item ->
        authUserVm.getAuthUserId()?.let {
            profileVm.deleteVlog(it, item.vlog.data.id)
        }
    }

    private fun updateProfileVlogs(res: Resource<List<UserVlogItem>>) = res.run {
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
                    message?.let {
                        snackBar.setText(it).show()
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
