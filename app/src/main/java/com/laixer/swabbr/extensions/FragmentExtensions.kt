package com.laixer.swabbr.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.profile.ProfileFragmentDirections

/**
 *  Builds a generic toast message and displays it.
 *
 *  @param message The message to display.
 */
fun Fragment.showMessage(message: String) = Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()

/**
 *  Callback which navigates us to a profile of a user.
 */
fun Fragment.onClickProfile(): (UserItem) -> Unit = {
    val action = ProfileFragmentDirections.actionGlobalProfileFragment(userId = it.id.toString())
    findNavController().navigate(action)
}

/**
 *  Callback which navigates us to a profile of a user.
 */
fun Fragment.onClickProfileWithRelation(): (UserWithRelationItem) -> Unit = {
    val action = ProfileFragmentDirections.actionGlobalProfileFragment(userId = it.user.id.toString())
    findNavController().navigate(action)
}
