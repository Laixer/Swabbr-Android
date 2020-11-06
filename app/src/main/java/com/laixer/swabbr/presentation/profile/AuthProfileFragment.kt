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
import com.laixer.swabbr.utils.loadAvatar
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.UserStatisticsItem
import kotlinx.android.synthetic.main.fragment_auth_profile.*
import kotlinx.android.synthetic.main.include_user_details.*
import kotlinx.android.synthetic.main.include_user_stats.*

class AuthProfileFragment : AuthFragment() {

    private var profileTabAdapter: ProfileTabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        authUserVm.user.observe(viewLifecycleOwner, Observer { updateProfile(it) })
        authUserVm.statistics.observe(viewLifecycleOwner, Observer { updateStats(it) })

        return inflater.inflate(R.layout.fragment_auth_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileTabAdapter = ProfileTabAdapter(this)
        pager.adapter = profileTabAdapter
        pager.offscreenPageLimit = 4

        authUserVm.getSelf(refresh = false)
        authUserVm.getStatistics(refresh = false)

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

    private fun updateProfile(res: Resource<UserItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                // TODO: Loading state
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    user_avatar.loadAvatar(user.profileImage, user.id)
                    user_nickname.text = requireContext().getString(R.string.nickname, user.nickname)
                    user.firstName?.let {
                        user_username.text = requireContext().getString(R.string.full_name, it, user.lastName)
                        user_username.visibility = View.VISIBLE
                    }
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStats(res: Resource<UserStatisticsItem>) {
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
