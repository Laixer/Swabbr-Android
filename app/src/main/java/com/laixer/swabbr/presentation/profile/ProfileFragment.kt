package com.laixer.swabbr.presentation.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.UserUpdatablePropertiesItem
import com.laixer.swabbr.presentation.model.UserWithStatsItem
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.utils.encodeToBase64
import com.laixer.swabbr.utils.formatNumber
import com.laixer.swabbr.utils.loadAvatar
import com.laixer.swabbr.utils.reduceDragSensitivity
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.properties.Delegates

// TODO Swipe refresh listener here as well
/**
 *  Fragment for displaying generic user profile information.
 *  This fragment contains tabs for more specific user details
 *  and information display. If this profile displays the current
 *  user, additional information is displayed.
 */
class ProfileFragment : AuthFragment() {
    private val args: ProfileFragmentArgs by navArgs()
    private val profileVm: ProfileViewModel by viewModel()

    // TODO Is this the best solution? Might be...
    /**
     *  The id of the profile that we are looking at. If no user id has
     *  been specified, this is assigned as the current users id.
     */
    private val userId: UUID by lazy {
        if (args.userId.isBlank()) {
            authUserVm.getSelfId()
        } else {
            UUID.fromString(args.userId)
        }
    }

    /**
     *  Indicates if we are looking at the currently authenticated user.
     */
    private var isSelf by Delegates.notNull<Boolean>()

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

        profileVm.user.observe(viewLifecycleOwner, Observer { onUserUpdated(it) })

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    /**
     *  Setup for the tabs and viewpager displaying the tabs.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Determine if we are looking at the current user.
        isSelf = authUserVm.getSelfId() == userId

        // Reduce swiping sensitivity for tabs.
        viewpager_user_profile.reduceDragSensitivity()

        // Always refresh all data about the user for correct display.
        profileVm.clearResources()
        profileVm.getUser(userId)

        /** Setup the tab viewpager based on [isSelf]. */
        viewpager_user_profile.adapter = ProfileTabAdapter(this, userId, isSelf)
        viewpager_user_profile.offscreenPageLimit = if (isSelf) 4 else 3 // Don't discard any tabs ever.

        /** Setup the tab layout based on [isSelf]. */
        TabLayoutMediator(tab_layout_user_profile, viewpager_user_profile) { tab, position ->
            if (isSelf) {
                tab.text = when (position) {
                    0 -> requireContext().getString(R.string.tab_vlogs)
                    1 -> requireContext().getString(R.string.tab_profile)
                    2 -> requireContext().getString(R.string.tab_following)
                    3 -> requireContext().getString(R.string.tab_followers)
                    else -> "UNDEFINED"
                }
            } else {
                tab.text = when (position) {
                    0 -> requireContext().getString(R.string.tab_vlogs)
                    1 -> requireContext().getString(R.string.tab_following)
                    2 -> requireContext().getString(R.string.tab_followers)
                    else -> "UNDEFINED"
                }
            }
        }.attach()
    }

    // TODO Move to details fragment?
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

    // TODO Move to details fragment?
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
     *  This function is attached to the observable stats object
     *  in the [authUserVm]. Whenever the current stats object in
     *  [authUserVm] changes, this function gets called.
     *
     *  @param res The user resource.
     */
    private fun onUserUpdated(res: Resource<UserWithStatsItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    // User information
                    user_profile_profile_image_insettings.loadAvatar(user.profileImage, user.id)
                    user_profile_displayed_name.text = user.getDisplayName()
                    user_profile_nickname.text = requireContext().getString(R.string.nickname, user.nickname)

                    // User stats
                    user_profile_followers_count.text = requireContext().formatNumber(user.totalFollowers)
                    user_profile_following_count.text = requireContext().formatNumber(user.totalFollowing)
                    user_profile_vlog_count.text = requireContext().formatNumber(user.totalVlogs)
                    user_profile_views.text = requireContext().formatNumber(user.totalViews)
                    user_profile_likes_received.text = requireContext().formatNumber(user.totalLikesReceived)
                    user_profile_reactions_received.text = requireContext().formatNumber(user.totalReactionsReceived)
                }
            }
            ResourceState.ERROR -> {
                showMessage(res.message ?: "")
            }
        }
    }

    // TODO
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_userprofile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // TODO
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_dest -> findNavController().navigate(ProfileFragmentDirections.actionViewSettings())
        }
        return super.onOptionsItemSelected(item)
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
