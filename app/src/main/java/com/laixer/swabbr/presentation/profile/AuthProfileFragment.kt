package com.laixer.swabbr.presentation.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.UserUpdatablePropertiesItem
import com.laixer.swabbr.presentation.model.UserWithStatsItem
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.utils.encodeToBase64
import com.laixer.swabbr.utils.formatNumber
import com.laixer.swabbr.utils.loadAvatar
import com.laixer.swabbr.utils.reduceDragSensitivity
import kotlinx.android.synthetic.main.fragment_auth_profile.*

// TODO Make generic for any user.
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

        // TODO Is this the way to go? I think not
        // Show the top action bar displaying the application name.
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        // Reduce swiping sensitivity for tabs
        viewpager_user_profile.reduceDragSensitivity()

        profileTabAdapter = ProfileTabAdapter(this)
        viewpager_user_profile.adapter = profileTabAdapter
        viewpager_user_profile.offscreenPageLimit = 4

        authUserVm.getSelf(refresh = false)
        authUserVm.getStatistics(refresh = false)

        TabLayoutMediator(tab_layout, viewpager_user_profile) { tab, position ->
            tab.text = when (position) {
                0 -> requireContext().getString(R.string.tab_vlogs)
                1 -> requireContext().getString(R.string.tab_profile)
                2 -> requireContext().getString(R.string.tab_following)
                3 -> requireContext().getString(R.string.tab_followers)
                else -> requireContext().getString(R.string.tab_profile) // TODO Look into this
            }
        }.attach()
    }

    /**
     *  Called by the [ImagePicker] activity. The result data contains the selected bitmap.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImagePicker.onActivityResult(
            context = this.requireContext(),
            resultCode = resultCode,
            data = data,
            successCallback = this::updateProfileImage
        )
    }

    /**
     *  Called when we have selected a new profile image.
     *
     *  @param bitmapSelected The new profile image.
     */
    private fun updateProfileImage(bitmapSelected: Bitmap) {
        user_profile_profile_image_insettings.setImageBitmap(bitmapSelected)
        authUserVm.updateGetSelf(UserUpdatablePropertiesItem(profileImage = bitmapSelected.encodeToBase64()))
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
                    user_profile_profile_image_insettings.loadAvatar(user.profileImage, user.id)
                    user_profile_displayed_name.text = requireContext().getString(
                        R.string.nickname,
                        user.nickname
                    ) // TODO Extend to get name in whatever format is present
                    user_profile_nickname.text = requireContext().getString(R.string.nickname, user.nickname)
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
                // loading_icon_profile_details.
            }
            ResourceState.SUCCESS -> {
                res.data?.let { stats ->
                    user_profile_followers_count.text = requireContext().formatNumber(stats.totalFollowing)
                    user_profile_following_count.text = requireContext().formatNumber(stats.totalFollowers)
                    user_profile_vlog_count.text = requireContext().formatNumber(stats.totalVlogs)
                    user_profile_views.text = requireContext().formatNumber(stats.totalViews)
                    user_profile_likes_received.text = requireContext().formatNumber(stats.totalLikes)
                    user_profile_reactions_received.text = requireContext().formatNumber(stats.totalReactionsReceived)
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
        viewpager_user_profile.adapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
