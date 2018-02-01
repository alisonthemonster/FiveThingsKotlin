package alison.fivethingskotlin

import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Intent
import android.os.Bundle

class PromoActivity : AccountAuthenticatorActivity() {

    private val ACCOUNT_TYPE = "FIVE_THINGS"
    private val SIGN_IN = 0
    private val CREATE_ACCOUNT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        val accountManager = AccountManager.get(this)
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        if (accounts.isEmpty()) {
            //no accounts can be found, send to create account screen
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivityForResult(intent, CREATE_ACCOUNT)
        } else {
            //an account was found
            val account = accounts[0]
            accountManager.getAuthToken(account, ACCOUNT_TYPE, Bundle(), this, OnTokenAcquired(), null) //TODO add onError handler instead of null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CREATE_ACCOUNT -> {
                if (resultCode == Activity.RESULT_OK) {
                    //TODO get auth token or load five things?
                } else {
                    //TODO handle error
                }
            }
            SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    //TODO get auth token or load five things?
                } else {
                    //TODO handle error
                }
            }
        }
    }

    private inner class OnTokenAcquired: AccountManagerCallback<Bundle> {

        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            if (bundle.getString(AccountManager.KEY_INTENT) != null) {
                //no token found on device and a reattempt for log in didn't work
                //show log in screen to force manual entry
                val intent = Intent(applicationContext, LoginActivity::class.java) //TODO double check this is right context
                startActivityForResult(intent, SIGN_IN)
                return
            }
            val token = bundle.getString(AccountManager.KEY_AUTHTOKEN)
            val intent = Intent(applicationContext, ContainerActivity::class.java) //TODO double check this is right context
            startActivity(intent)
        }

    }

}
