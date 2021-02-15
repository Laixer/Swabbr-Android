package com.laixer.swabbr.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

// TODO Can this go entirely?
/**
 *  View model for displaying the currently authenticated user.
 */
open class AuthUserViewModel constructor(
    private val userManager: UserManager,
    private val authUserUseCase: AuthUserUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {
    // TODO We might want to store this in the usermanager
    /**
     *  Get the id of the current user.
     */
    fun getSelfId(): UUID = this.authUserUseCase.getSelfId()
}
