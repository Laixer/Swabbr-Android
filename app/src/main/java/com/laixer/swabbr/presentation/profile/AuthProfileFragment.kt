package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.UserWithStatsItem
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.fragment_auth_profile.*
import kotlinx.android.synthetic.main.include_user_details.*
import kotlinx.android.synthetic.main.include_user_stats.*

/**
 *  Fragment for displaying generic user profile information.
 *  This fragment contains tabs for more specific user details
 *  and information display.
 */
class AuthProfileFragment : AuthFragment() {

    private var profileTabAdapter: ProfileTabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    /**
     *  Binds update functions to observable resources in the
     *  [authUserVm].
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        authUserVm.user.observe(viewLifecycleOwner, Observer { updatePropertiesFromViewModel(it) })
        authUserVm.statistics.observe(viewLifecycleOwner, Observer { updateStatsFromViewModel(it) })

        return inflater.inflate(R.layout.fragment_auth_profile, container, false)
    }

    /**
     *  Setup for the tabs.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileTabAdapter = ProfileTabAdapter(this)
        pager.adapter = profileTabAdapter
        pager.offscreenPageLimit = 4

        authUserVm.getSelf(refresh = true) // TODO Was false, look at this
        authUserVm.getStatistics(refresh = true) // TODO Was false, look at this

        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = when (position) {
                0 -> "Vlogs"
                1 -> "Profile"
                2 -> "Following"
                3 -> "Invites"
                else -> "Profile?"
            }
        }.attach()
    }

    /**
     *  This function is attached to the observable user object
     *  in the [authUserVm]. Whenever the current user object in
     *  [authUserVm] changes, this function gets called.
     *
     *  Note that this function does not actually perform any user
     *  updating operation. It just syncs the UI with [authUserVm].
     *
     *  @param res The user resource.
     */
    private fun updatePropertiesFromViewModel(res: Resource<UserCompleteItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                // TODO: Loading state
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    user_avatar.loadAvatar(user.profileImage, user.id)
                    user_displayed_name.text = requireContext().getString(R.string.nickname, user.nickname)
                    user.firstName?.let {
                        user_nickname.text = requireContext().getString(R.string.full_name, it, user.lastName)
                        user_nickname.visibility = View.VISIBLE
                    }
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *  This function is attached to the observable stats object
     *  in the [authUserVm]. Whenever the current stats object in
     *  [authUserVm] changes, this function gets called.
     *
     *  @param res The user resource.
     */
    private fun updateStatsFromViewModel(res: Resource<UserWithStatsItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                // TODO: Loading state
            }
            ResourceState.SUCCESS -> {
                res.data?.let { stats ->
                    following_count.text = requireContext().getString(R.string.following_count, stats.totalFollowing)
                    followers_count.text = requireContext().getString(R.string.followers_count, stats.totalFollowers)
                    vlog_count.text = requireContext().getString(R.string.count, stats.totalVlogs)
                    view_count.text = requireContext().getString(R.string.count, stats.totalViews)
                    like_count.text = requireContext().getString(R.string.count, stats.totalLikes)
                    reaction_count.text = requireContext().getString(R.string.count, stats.totalReactionsReceived)
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_userprofile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_dest -> findNavController().navigate(AuthProfileFragmentDirections.actionViewSettings())
        }
        return super.onOptionsItemSelected(item)
    }

    internal class ProfileTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AuthProfileVlogsFragment()
                1 -> AuthProfileDetailsFragment()
                2 -> AuthProfileFollowingFragment()
                3 -> AuthProfileRequestsFragment()
                else -> AuthProfileDetailsFragment()
            }
        }

        override fun getItemCount(): Int = 4
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileTabAdapter = null
        pager.adapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
