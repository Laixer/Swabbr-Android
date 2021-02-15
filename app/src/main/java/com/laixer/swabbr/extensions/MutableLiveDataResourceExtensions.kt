package com.laixer.swabbr.extensions

import androidx.lifecycle.MutableLiveData
import com.laixer.presentation.Resource
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import java.util.*

/**
 *  TODO This is a fix for a design flaw in the application design by the client.
 *       The same user can exist multiple times in the [likingUserWrappers] so we
 *       can have multiple follow buttons for the same user. The current fix is
 *       that all buttons disappear once we click one of them. See the issue at
 *       https://github.com/Laixer/Swabbr-Android/issues/141
 *  When we follow a user based on a list of [UserWithRelationItem] objects, this
 *  follow action should be updated locally in the resource list. This method does
 *  that by setting the follow request status to [newStatus] for each item in the
 *  list which has user id equal to [userId].
 *
 *  After doing this, the [setSuccess] method is called on the resource so any
 *  observers will be notified of any changes made. If the resource list contains
 *  no items, this does nothing. This situation should not occur because we are
 *  attempting to update the entire list based on an interaction with one of its
 *  items, hence it shouldn't be empty.
 *
 *  @param userId The user id to update the follow request status for.
 */
fun MutableLiveData<Resource<List<UserWithRelationItem>>>.cascadeFollowAction(
    userId: UUID,
    newStatus: FollowRequestStatus
) = this.value?.data?.let { list ->
    list.forEach { item ->
        if (item.user.id == userId) {
            item.followRequestStatus = newStatus
        }
    }

    this.setSuccess(list)
}

/**
 *  Call [setSuccess] if we have any data present, else do nothing.
 */
fun <T: Any> MutableLiveData<Resource<T>>.setSuccessAgain() {
    this.value?.data?.let { data ->
        this.setSuccess(data)
    }
}
