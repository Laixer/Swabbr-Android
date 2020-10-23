package com.laixer.swabbr.presentation.auth

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import com.auth0.android.jwt.JWT
import com.google.firebase.iid.FirebaseInstanceId
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.AuthActivity
import com.laixer.swabbr.presentation.auth.UserManager.Companion.ACCOUNT_TYPE
import com.laixer.swabbr.presentation.auth.UserManager.Companion.KEY_AUTH_TOKEN_TYPE
import com.laixer.swabbr.presentation.auth.UserManager.Companion.KEY_CONFIRM_ACCOUNT
import com.laixer.swabbr.presentation.auth.UserManager.Companion.KEY_CREATE_ACCOUNT
import com.laixer.swabbr.presentation.auth.UserManager.Companion.KEY_FEATURES
import com.laixer.swabbr.presentation.auth.login.LoginFragment

class SimpleAuthenticator(
    private val context: Context,
    private val accountManager: AccountManager,
    private val usecase: AuthUseCase
) : AbstractAccountAuthenticator(context) {

    override fun getAuthTokenLabel(authTokenType: String): String? {
        return authTokenType
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle?
    ): Bundle? {
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtras(
            bundleOf(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE to response,
                AccountManager.KEY_ACCOUNT_NAME to account.name,
                AccountManager.KEY_ACCOUNT_TYPE to account.type,
                AccountManager.KEY_USERDATA to options,
                KEY_CONFIRM_ACCOUNT to true
            )
        )

        return bundleOf(AccountManager.KEY_INTENT to intent)
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle? {
        var authToken = accountManager.peekAuthToken(account, authTokenType)

        if (authToken.isNullOrBlank()) {
            accountManager.getPassword(account)?.let {
                try {
                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                        require(task.isSuccessful) { "Unable to identify this device on Firebase" }
                        authToken = usecase.login(
                            account.name,
                            it,
                            task.result!!.token
                        ).blockingGet().jwtToken?.toString()
                    }
                } catch (e: Throwable) {
                    throw NetworkErrorException(e)
                }
            }
        }

        if (!authToken.isNullOrBlank()) {
            val jwt = JWT(authToken)

            if (!jwt.isExpired(0L)) {
                return bundleOf(
                    AccountManager.KEY_ACCOUNT_NAME to account.name,
                    AccountManager.KEY_ACCOUNT_TYPE to account.type,
                    AccountManager.KEY_AUTHTOKEN to authToken
                )
            } else {
                accountManager.invalidateAuthToken(ACCOUNT_TYPE, authToken)
            }
        }


        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtras(
            bundleOf(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE to response,
                AccountManager.KEY_ACCOUNT_NAME to account.name,
                AccountManager.KEY_ACCOUNT_TYPE to account.type,
                AccountManager.KEY_USERDATA to options,
                KEY_AUTH_TOKEN_TYPE to authTokenType
            )
        )

        return bundleOf(AccountManager.KEY_INTENT to intent)
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<out String>
    ): Bundle? {
        return null
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle? {
        return null
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle
    ): Bundle? {
        val intent = Intent(context, LoginFragment::class.java)
        intent.putExtras(
            bundleOf(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE to response,
                AccountManager.KEY_ACCOUNT_TYPE to accountType,
                AccountManager.KEY_USERDATA to options,
                KEY_AUTH_TOKEN_TYPE to authTokenType,
                KEY_FEATURES to requiredFeatures,
                KEY_CREATE_ACCOUNT to true
            )
        )

        return bundleOf(AccountManager.KEY_INTENT to intent)
    }

    internal companion object {
        val TAG = SimpleAuthenticator::class.simpleName
    }
}
