package com.laixer.swabbr.extensions

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
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

/**
 *  Hides the soft keyboard. Android provides no support for managing the soft
 *  input visibility in the way that we require, so this method has been added
 *  as a solution to hide it.
 *
 *  TODO Ignoring the docs with regards to hiding soft input programmatically
 *       https://github.com/Laixer/Swabbr-Android/issues/197
 */
fun Fragment.hideSoftKeyboard() {
    view?.clearFocus()
    val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}
