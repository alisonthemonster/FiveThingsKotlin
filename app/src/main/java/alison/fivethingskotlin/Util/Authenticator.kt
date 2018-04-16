package alison.fivethingskotlin.Util

import alison.fivethingskotlin.LogInActivity
import alison.fivethingskotlin.Util.Constants.ACCOUNT_TYPE
import alison.fivethingskotlin.Util.Constants.AUTH_TOKEN_TYPE
import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log


class Authenticator(private val mContext: Context) : AbstractAccountAuthenticator(mContext) {

    // Editing properties is not supported
    override fun editProperties(r: AccountAuthenticatorResponse, s: String): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(response: AccountAuthenticatorResponse,
                            accountType: String,
                            authTokenType: String,
                            requiredFeatures: Array<String>,
                            options: Bundle): Bundle {

        val intent = Intent(mContext, LogInActivity::class.java)
        intent.putExtra(ACCOUNT_TYPE, accountType)
        intent.putExtra(AUTH_TOKEN_TYPE, authTokenType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, bundle: Bundle): Bundle {
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

        //user has created an account on device but has never logged in to generate a token
        val intent = Intent(mContext, LogInActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(ACCOUNT_TYPE, account.type)
        intent.putExtra(AUTH_TOKEN_TYPE, authTokenType)

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
}