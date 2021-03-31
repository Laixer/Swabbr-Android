package com.laixer.swabbr.domain.usecase

import android.net.Uri
import com.laixer.swabbr.domain.interfaces.UserRepository
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.services.uploading.UploadHelper.Companion.uploadFile
import com.laixer.swabbr.services.users.UserManager
import com.laixer.swabbr.utils.media.MediaConstants
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Use case with regards to the currently authenticated user.
 *  This is responsible for operations on said user.
 */
class AuthUserUseCase constructor(
    private val userRepository: UserRepository,
    private val userManager: UserManager
) {
    /**
     *  Gets the id of the currently authenticated user. Only call this if
     *  we are authenticated.
     */
    fun getSelfId(): UUID = userManager.getUserIdOrNull() ?: UUID.randomUUID() // TODO Horrible solution

    /**
     *  Get the currently authenticated user. This contains
     *  personal details as well.
     *
     *  @param refresh Force a data refresh.
     */
    fun getSelf(refresh: Boolean): Single<UserComplete> = userRepository.getSelf(refresh)

    /**
     *  Update the currently authenticated user.
     *
     *  @param user User with updated properties.
     *  @param imageUploadUri If the [user] also contains a profile image file, also
     *                        specify the uri to upload to. Else nothing happens.
     */
    fun updateSelf(user: UserUpdatableProperties, imageUploadUri: Uri? = null): Completable =
        if (user.profileImageFile != null && imageUploadUri != null) {
            Completable.fromAction {
                uploadFile(user.profileImageFile, imageUploadUri, MediaConstants.IMAGE_JPEG_MIME_TYPE)
            }.andThen(userRepository.update(user))
        } else {
            userRepository.update(user)
        }

    companion object {
        val TAG = AuthUserUseCase::class.java.simpleName
    }
}
