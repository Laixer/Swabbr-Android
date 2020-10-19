package com.laixer.swabbr.presentation.auth

import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import io.reactivex.disposables.CompositeDisposable
import java.util.*

open class AuthUserViewModel constructor(
    private val userManager: UserManager
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun getAuthToken(): JWT? = userManager.token
    fun getAuthUserId(): UUID? = userManager.getUserProperty("id")?.let(UUID::fromString)

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
