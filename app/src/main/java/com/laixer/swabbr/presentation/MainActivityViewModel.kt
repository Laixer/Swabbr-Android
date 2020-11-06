package com.laixer.swabbr.presentation

import android.accounts.AccountManagerCallback
import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.presentation.auth.UserManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class MainActivityViewModel constructor(
    private val userManager: UserManager
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val authToken = MutableLiveData<Resource<JWT>>()

    init {
        compositeDisposable.add(
            userManager.statusObservable
                .doOnSubscribe { authToken.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { userManager.token?.let { authToken.setSuccess(it) }
                        ?: authToken.setError("Auth token was null")
                    },
                    { authToken.setError(it.message) }
                )
        )
    }

    fun probeAuthToken() {
        val token = userManager.token
        if (token === null) {
            authToken.setError("token null")
        } else {
            if (token.isExpired(0L)) {
                authToken.setError("token expired")
            }
        }

    }

    fun invalidateSession() {
        userManager.invalidate()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }


}
