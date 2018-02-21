package alison.fivethingskotlin

import alison.fivethingskotlin.Util.Constants.ACCOUNT_TYPE
import alison.fivethingskotlin.Util.Constants.AUTH_TOKEN_TYPE
import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_promo.*

class PromoActivity : AppCompatActivity() {

    private val SIGN_IN = 0
    private val CREATE_ACCOUNT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        val accountManager = AccountManager.get(this)
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        if (!accounts.isEmpty()) {
            //an account was found
            Log.d("blerg", "accounts: " + accounts)
            Log.d("blerg", "accounts: " + accountManager.getAccountsByType(ACCOUNT_TYPE).size)
            val account = accounts[0]
            accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, Bundle(), this, OnTokenAcquired(), null) //TODO add onError handler instead of null
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d("blerg", "request code: " + requestCode)
        when (requestCode) {
            CREATE_ACCOUNT -> {
                Log.d("blerg", "onActivityResult with CREATE_ACCOUNT")
                if (resultCode == Activity.RESULT_OK) {
                    //user was able to create an account
                    //but now they need to activate their account via email
                    Toast.makeText(this, "Check your email to validate your account!", Toast.LENGTH_LONG).show()
//                    val accountManager = AccountManager.get(this)
//                    val account = data.getParcelableExtra<Account>("ACCOUNT")
//                    accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, Bundle(), this, OnTokenAcquired(), null) //TODO add onError handler instead of null
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
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivityForResult(intent, SIGN_IN)
                return
            }
            Log.d("blerg", "BLESSED TOKEN HAS BEEN RECEIVED! About to fully open app")
            val token = bundle.getString(AccountManager.KEY_AUTHTOKEN)
            Log.d("blerg", "DA TOKEN???? " + token)
            val intent = Intent(applicationContext, ContainerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

}
