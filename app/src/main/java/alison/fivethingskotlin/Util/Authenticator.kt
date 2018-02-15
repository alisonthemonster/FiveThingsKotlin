package alison.fivethingskotlin.Util

import alison.fivethingskotlin.LoginActivity
import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log


class Authenticator(internal var mContext: Context) : AbstractAccountAuthenticator(mContext) {

    // Editing properties is not supported
    override fun editProperties(r: AccountAuthenticatorResponse, s: String): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(response: AccountAuthenticatorResponse,
                            accountType: String,
                            authTokenType: String,
                            requiredFeatures: Array<String>,
                            options: Bundle): Bundle {

        val intent = Intent(mContext, LoginActivity::class.java)
        intent.putExtra("FIVE_THINGS", accountType) //TODO move FIVE_THINGS somewhere global
        intent.putExtra("full_access", authTokenType)
        intent.putExtra("is_adding_new_account", true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    @Throws(NetworkErrorException::class) //TODO is this needed?
    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, bundle: Bundle): Bundle {

        Log.d("blerg", "inside getAuthToken with authTokenType: " + authTokenType)

        val accountManager = AccountManager.get(mContext)
        val authToken = accountManager.peekAuthToken(account, authTokenType)

        if (!TextUtils.isEmpty(authToken)) {
            //we found a token and its all good
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            Log.d("blerg", "built the bundle with found token")
            return result
        }

        //we found an old token in the "password"
        //we can use it to make a refresh call
        val password = accountManager.getPassword(account)
        if (password != null) {
            Log.d("blerg", "going to try to refresh token!")
            val newToken = "blah" //TODO call refresh endpoint
            val refreshToken = "bleepbloop"
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            bundle.putString(AccountManager.KEY_AUTHTOKEN, newToken)
            accountManager.setPassword(account, refreshToken)
            return bundle

        }

        //we couldn't get a token
        Log.d("blerg", "auth token wasn't retrieved from cache")
        val intent = Intent(mContext, LoginActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra("FIVE_THINGS", account.type)
        intent.putExtra("full_access", authTokenType)

        val retBundle = Bundle()
        retBundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return retBundle
    }

    // Ignore attempts to confirm credentials
    override fun confirmCredentials(r: AccountAuthenticatorResponse, account: Account, bundle: Bundle): Bundle? {
        return null
    }

    // Getting a label for the auth token is not supported
    override fun getAuthTokenLabel(authTokenType: String): String {
        throw UnsupportedOperationException()
    }

    // Updating user credentials is not supported
    override fun updateCredentials(r: AccountAuthenticatorResponse, account: Account, s: String, bundle: Bundle): Bundle {
        throw UnsupportedOperationException()
    }

    // Checking features for the account is not supported
    override fun hasFeatures(r: AccountAuthenticatorResponse, account: Account, strings: Array<String>): Bundle {
        throw UnsupportedOperationException()
    }

    // Handle a user logging out here.
    override fun getAccountRemovalAllowed(response: AccountAuthenticatorResponse, account: Account): Bundle {
        return super.getAccountRemovalAllowed(response, account)
    }
}