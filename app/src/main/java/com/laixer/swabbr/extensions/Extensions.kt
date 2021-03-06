package com.laixer.swabbr.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.util.SparseArray
import android.widget.ImageView
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.utils.todosortme.loadImageUrl
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.Duration

/**
 *  Gets the profile image for a user and assigns it.
 */
fun ImageView.loadAvatarFromUser(user: UserItem) = loadAvatarFromUser(user.mapToDomain())

/**
 *  Gets the profile image for a user and assigns it.
 */
fun ImageView.loadAvatarFromUser(user: User) =
    try {
        if (user.hasProfileImage && user.profileImageUri != null) {
            loadImageUrl(URL(user.profileImageUri.toString()))
        } else {
            loadImageUrl(URL("https://api.hello-avatar.com/adorables/285/${user.id}"))
        }
    } catch (e: Exception) {
        Log.e("loadAvatarFromUser", "Exception while loading profile image for user ${user.id} - ${user.hasProfileImage} - ${user.profileImageUri}")
    }

/**
 * Clear the image of an image view.
 */
fun ImageView.clearAvatar() {
    this.background = null
}

/**
 * Manages the various graphs needed for a [BottomNavigationView].
 *
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */
@Suppress("LongMethod", "ComplexMethod")
fun BottomNavigationView.setupWithNavController(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
): LiveData<NavController> {
    // Map of tags
    val graphIdToTagMap = SparseArray<String>()
    // Result. Mutable live data with the selected controlled
    val selectedNavController = MutableLiveData<NavController>()
    var firstFragmentGraphId = 0
    // First create a NavHostFragment for each NavGraph ID
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)
        // Find or create the Navigation host fragment
        val navHostFragment = obtainNavHostFragment(fragmentManager, fragmentTag, navGraphId, containerId)
        // Obtain its id
        val graphId = navHostFragment.navController.graph.id

        if (index == 0) {
            firstFragmentGraphId = graphId
        }
        // Save to the map
        graphIdToTagMap[graphId] = fragmentTag
        // Attach or detach nav host fragment depending on whether it's the selected item.
        if (this.selectedItemId == graphId) {
            // Update livedata with the selected graph
            selectedNavController.value = navHostFragment.navController
            attachNavHostFragment(fragmentManager, navHostFragment, index == 0)
        } else {
            detachNavHostFragment(fragmentManager, navHostFragment)
        }
    }
    // Now connect selecting an item with swapping Fragments
    var selectedItemTag = graphIdToTagMap[this.selectedItemId]
    val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
    var isOnFirstFragment = selectedItemTag == firstFragmentTag
    // When a navigation item is selected
    setOnNavigationItemSelectedListener { item ->
        // Don't do anything if the state is state has already been saved.
        if (fragmentManager.isStateSaved) {
            false
        } else {
            val newlySelectedItemTag = graphIdToTagMap[item.itemId]
            if (selectedItemTag != newlySelectedItemTag) {
                // Pop everything above the first fragment (the "fixed start destination")
                fragmentManager.popBackStack(
                    firstFragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment
                // Exclude the first fragment tag because it's always in the back stack.
                if (firstFragmentTag != newlySelectedItemTag) {
                    // Commit a transaction that cleans the back stack and adds the first fragment
                    // to it, creating the fixed started destination.
                    fragmentManager.beginTransaction().attach(selectedFragment)
                        .setPrimaryNavigationFragment(selectedFragment).apply {
                            // Detach all other Fragments
                            graphIdToTagMap.forEach { _, fragmentTagIter ->
                                if (fragmentTagIter != newlySelectedItemTag) {
                                    detach(fragmentManager.findFragmentByTag(firstFragmentTag)!!)
                                }
                            }
                        }.addToBackStack(firstFragmentTag).setReorderingAllowed(true).commit()
                }
                selectedItemTag = newlySelectedItemTag
                isOnFirstFragment = selectedItemTag == firstFragmentTag
                selectedNavController.value = selectedFragment.navController
                true
            } else {
                false
            }
        }
    }
    // Optional: on item reselected, pop back stack to the destination of the graph
    setupItemReselected(graphIdToTagMap, fragmentManager)
    // Handle deep link
    setupDeepLinks(navGraphIds, fragmentManager, containerId, intent)
    // Finally, ensure that we update our BottomNavigationView when the back stack changes
    fragmentManager.addOnBackStackChangedListener {
        if (!isOnFirstFragment && !fragmentManager.isOnBackStack(firstFragmentTag)) {
            this.selectedItemId = firstFragmentGraphId
        }
        // Reset the graph if the currentDestination is not valid (happens when the back
        // stack is popped after using the back button).
        selectedNavController.value?.let { controller ->
            if (controller.currentDestination == null) {
                controller.navigate(controller.graph.id)
            }
        }
    }
    return selectedNavController
}

private fun BottomNavigationView.setupDeepLinks(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
) {
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)
        // Find or create the Navigation host fragment
        val navHostFragment = obtainNavHostFragment(
            fragmentManager, fragmentTag, navGraphId, containerId
        )
        // Handle Intent
        if (navHostFragment.navController.handleDeepLink(intent) &&
            selectedItemId != navHostFragment.navController.graph.id
        ) {
            this.selectedItemId = navHostFragment.navController.graph.id
        }
    }
}

fun encodeImageToBase64(byteArray: ByteArray): String = Base64.encodeToString(byteArray, Base64.DEFAULT)

fun decodeStringFromBase64(base64: String): ByteArray = Base64.decode(base64, Base64.DEFAULT)

fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray = with(ByteArrayOutputStream()) {
    bitmap.compress(Bitmap.CompressFormat.WEBP, 100, this)
    return this.toByteArray()
}

fun convertBase64ToBitmap(base64: String): Bitmap = convertByteArrayToBitmap(decodeStringFromBase64(base64))

fun convertBitmapToBase64(bitmap: Bitmap): String = encodeImageToBase64(convertBitmapToByteArray(bitmap))

private fun BottomNavigationView.setupItemReselected(
    graphIdToTagMap: SparseArray<String>,
    fragmentManager: FragmentManager
) {
    setOnNavigationItemReselectedListener { item ->
        val newlySelectedItemTag = graphIdToTagMap[item.itemId]
        val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment
        val navController = selectedFragment.navController
        // Pop the back stack to the start destination of the current navController graph
        navController.popBackStack(
            navController.graph.startDestination, false
        )
    }
}

private fun detachNavHostFragment(fragmentManager: FragmentManager, navHostFragment: NavHostFragment) {
    fragmentManager.beginTransaction().detach(navHostFragment).commitNow()
}

private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
    isPrimaryNavFragment: Boolean
) {
    fragmentManager.beginTransaction().attach(navHostFragment).apply {
        if (isPrimaryNavFragment) {
            setPrimaryNavigationFragment(navHostFragment)
        }
    }.commitNow()
}

private fun obtainNavHostFragment(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    navGraphId: Int,
    containerId: Int
): NavHostFragment {
    // If the Nav Host fragment exists, return it
    val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingFragment?.let { return it }
    // Otherwise, create it and return it.
    val navHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction().add(containerId, navHostFragment, fragmentTag).commitNow()
    return navHostFragment
}

private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount) {
        if (getBackStackEntryAt(index).name == backStackName) {
            return true
        }
    }
    return false
}

private fun getFragmentTag(index: Int) = "bottomNavigation#$index"

/**
 *  Gets the amount of seconds in the last minute of this duration.
 */
fun Duration.lastMinuteSeconds(): Long = seconds % 60

/**
 *  Gets the amount of minutes in this duration.
 */
fun Duration.minutes(): Long = (seconds / 60) - lastMinuteSeconds()
