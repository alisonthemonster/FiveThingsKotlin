package alison.fivethingskotlin

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class PromoActivity : AppCompatActivity() { //TODO should this be the AccountAuthenticatorActivity?

    private val ACCOUNT_TYPE = "FIVE_THINGS"
    private val SIGN_IN = 0
    private val CREATE_ACCOUNT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        val accountManager = AccountManager.get(this)
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        if (accounts.isEmpty()) {
            Log.d("blerg", "no accounts found")
            //no accounts can be found, send to create account screen
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivityForResult(intent, CREATE_ACCOUNT)
        } else {
            //an account was found
            Log.d("blerg", "accounts: " + accounts)
            val account = accounts[0]
            accountManager.getAuthToken(account, ACCOUNT_TYPE, Bundle(), this, OnTokenAcquired(), null) //TODO add onError handler instead of null
        }
    }

    //TODO a back press crashes the app

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d("blerg", "request code: " + requestCode)
        when (requestCode) {
            CREATE_ACCOUNT -> {
                Log.d("blerg", "onActivityResult with CREATE_ACCOUNT")
                if (resultCode == Activity.RESULT_OK) {
                    val accountManager = AccountManager.get(this)
                    val account = data.getParcelableExtra<Account>("ACCOUNT")
                    accountManager.getAuthToken(account, ACCOUNT_TYPE, Bundle(), this, OnTokenAcquired(), null) //TODO add onError handler instead of null

                    //TODO get auth token or load five things?
                    Log.d("blerg", "account created all good dude")
                } else {
                    //TODO handle error
                }
            }
            SIGN_IN -> {
                Log.d("blerg", "onActivityResult with SIGN_IN")
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("blerg", "account signed in all good dude")
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
                Log.d("blerg", "About to launch login activity, bc no good token found")
                //no token found on device, show log in screen to force manual entry
                val intent = Intent(applicationContext, LoginActivity::class.java) //TODO double check this is right context
                startActivityForResult(intent, SIGN_IN)
                return
            }
            Log.d("blerg", "BLESSED TOKEN HAS BEEN RECEIVED! About to fully open app")
            val token = bundle.getString(AccountManager.KEY_AUTHTOKEN)
            Log.d("blerg", "DA TOKEN???? " + token)
            val intent = Intent(applicationContext, ContainerActivity::class.java)
            startActivity(intent)
        }

    }

}
