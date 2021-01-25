package com.laixer.swabbr.presentation.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.OnAccountsUpdateListener
import android.app.Activity
import android.os.Bundle
import com.auth0.android.jwt.JWT
import com.laixer.cache.Cache
import com.laixer.swabbr.BuildConfig
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

// TODO Properly doc this!
/**
 *  User manager containing our authentication details.
 */
class UserManager(
    private val accountManager: AccountManager,
    val cache: Cache
) : OnAccountsUpdateListener {
    private val _statusObservable: BehaviorSubject<Boolean> = BehaviorSubject.create()
    val statusObservable: Observable<Boolean> = _statusObservable

    /**
     *  Location where the user login jwt token is stored.
     */
    val token: JWT?
        get() = cache.get(KEY_ACCOUNT_TOKEN) ?: getCurrentAccount()?.let { acc ->
            accountManager.peekAuthToken(acc, DEFAULT_AUTH_TOKEN_TYPE)?.let { JWT(it) }
        }

    init {
        accountManager.addOnAccountsUpdatedListener(this, null, true)
    }

    override fun onAccountsUpdated(accounts: Array<out Account>?) = with(isConnected()) {
        if (_statusObservable.value != this)
            _statusObservable.onNext(this)
    }

    fun disconnect(force: Boolean = false) {
        getCurrentAccount()?.let {
            invalidate()
            accountManager::clearPassword
        }

        if (_statusObservable.value != false || force)
            _statusObservable.onNext(false)
    }

    fun invalidate() {
        getCurrentAccount()?.let {
            accountManager.invalidateAuthToken(ACCOUNT_TYPE, accountManager.peekAuthToken(it, DEFAULT_AUTH_TOKEN_TYPE))
        }
        cache.remove(KEY_ACCOUNT_TOKEN)

        if (_statusObservable.value != false)
            _statusObservable.onNext(false)
    }

    fun JWT?.isTokenValid(): Boolean = this?.isExpired(0L)?.not() == true

    private fun isConnected() = this.token.isTokenValid()

    fun getUserProperty(key: String): String? {
        return getCurrentAccount()?.let { accountManager.getUserData(it, key) }
    }

    fun refreshToken(activity: Activity, callback: AccountManagerCallback<Bundle>) {
        getCurrentAccount()?.let {
            accountManager.invalidateAuthToken(ACCOUNT_TYPE, accountManager.peekAuthToken(it, DEFAULT_AUTH_TOKEN_TYPE))
            accountManager.getAuthToken(it, DEFAULT_AUTH_TOKEN_TYPE, null, false, callback, null)

            return
        }
        accountManager.getAuthTokenByFeatures(
            ACCOUNT_TYPE, DEFAULT_AUTH_TOKEN_TYPE,
            null,
            activity,
            null,
            null,
            callback,
            null
        )
    }

    /**
     * Only meant to be called when sign up api responds successfully.
     */
    internal fun createAccount(
        email: String,
        password: String,
        token: JWT? = null,
        extras: Bundle? = null
    ) {
        if (token != null) {
            connect(email, password, token, extras)
        } else if (accountManager.addAccountExplicitly(Account(email, ACCOUNT_TYPE), password, extras)) {
            cache.set(KEY_ACCOUNT_NAME, email)
        }
    }

    /**
     * Only meant to be called when sign in api responds successfully.
     *
     *  @param extras Additional user data to be stored in the account manager.
     */
    internal fun connect(email: String, password: String, token: JWT, extras: Bundle? = null) {
        val account = getAccount(email)

        if (account == null) {
            createAccount(email, password)
        }

        getAccount(email)?.let { acc ->
            accountManager.setPassword(acc, password)
            accountManager.setAuthToken(acc, DEFAULT_AUTH_TOKEN_TYPE, token.toString())

            extras?.let { bundle ->
                for (key in bundle.keySet()) {
                    accountManager.setUserData(acc, key, bundle.get(key).toString())
                }
            }

            cache.set(KEY_ACCOUNT_NAME, email)
            cache.set(KEY_ACCOUNT_TOKEN, token)

            if (_statusObservable.value != true) {
                _statusObservable.onNext(true)
            }
        }
    }

    private fun getAccount(email: String): Account? {
        accountManager.accounts.forEach {
            if (it.name == email) return it
        }

        return null
    }

    fun getCurrentAccount(): Account? {
        val accountName = cache.get<String>(KEY_ACCOUNT_NAME)

        if (accountName?.isBlank() == true) {
            return null
        }

        val accounts = accountManager.accounts
        accounts.forEach { account ->
            if (accountName == account.name) {
                return account
            }
        }

        cache.remove(KEY_ACCOUNT_NAME)
        return null
    }

    companion object {
        const val KEY_AUTH_TOKEN_TYPE = "authTokenType"
        const val KEY_FEATURES = "features"
        const val KEY_CREATE_ACCOUNT = "createAccount"
        const val KEY_CONFIRM_ACCOUNT = "confirmAccount"
        const val DEFAULT_AUTH_TOKEN_TYPE = "defaultAuthTokenType"
        const val ACCOUNT_TYPE = BuildConfig.APPLICATION_ID

        //        const val ACCOUNT_TYPE_GOOGLE = "com.google"
//        const val ACCOUNT_TYPE_FACEBOOK = "com.facebook.auth.login"
//        const val ACCOUNT_TYPE_TWITTER = "com.twitter.android.oauth.token"
        const val REQUEST_CODE_CHOOSE_ACCOUNT = 354

        const val KEY_ACCOUNT_NAME = "account_name"
        const val KEY_ACCOUNT_TOKEN = "account_token"

    }
}
