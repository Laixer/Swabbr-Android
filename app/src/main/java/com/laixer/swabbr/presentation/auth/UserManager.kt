package com.laixer.swabbr.presentation.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import com.laixer.cache.Cache
import com.laixer.swabbr.BuildConfig
import io.reactivex.subjects.BehaviorSubject

class UserManager(
    private val accountManager: AccountManager,
    val cache: Cache
): OnAccountsUpdateListener {
    private val _statusObservable: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val token: String?
        get() {
            val token = cache.get<String>(KEY_ACCOUNT_TOKEN)
            return if (token?.isNotBlank() == true) token
            else getCurrentAccount()?.let {
                accountManager.peekAuthToken(
                    it,
                    DEFAULT_AUTH_TOKEN_TYPE
                )
            }
        }

    init {
        accountManager.addOnAccountsUpdatedListener(this, null, true)
    }

    override fun onAccountsUpdated(accounts: Array<out Account>?) {
        val connected = isConnected()
        if (_statusObservable.value != connected)
            _statusObservable.onNext(connected)
    }

    fun disconnect() {
        getCurrentAccount()?.let {
            accountManager.invalidateAuthToken(
                ACCOUNT_TYPE,
                accountManager.peekAuthToken(it, DEFAULT_AUTH_TOKEN_TYPE)
            )
            accountManager.clearPassword(it)
            cache.remove(KEY_ACCOUNT_TOKEN)

            if (_statusObservable.value != false)
                _statusObservable.onNext(false)
        }
    }

    fun isConnected() = !token.isNullOrBlank()

    /**
     * Only meant to be called when sign up api responds successfully.
     */
    /*private*/internal fun createAccount(email: String, password: String, token: String? = null) {
        if (token != null) {
            connect(email, password, token)
        }
        else if (accountManager.addAccountExplicitly(
                Account(email, ACCOUNT_TYPE),
                password,
                null
            )
        ) {
            cache.set(KEY_ACCOUNT_NAME, email)
        }
    }

    /**
     * Only meant to be called when sign in api responds successfully.
     */
    /*private*/internal fun connect(email: String, password: String, token: String) {
        val account = getAccount(email)

        if (account == null)
            createAccount(email, password)

        getAccount(email)?.let {
            accountManager.setPassword(it, password)
            accountManager.setAuthToken(it, DEFAULT_AUTH_TOKEN_TYPE, token)
            cache.set(KEY_ACCOUNT_NAME, email)
            cache.set(KEY_ACCOUNT_TOKEN, token)

            if (_statusObservable.value != true)
                _statusObservable.onNext(true)
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

        if (accountName?.isBlank() == true) return null

        val accounts = accountManager.accounts
        accounts.forEach { account ->
            if (accountName == account.name)
                return account
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
