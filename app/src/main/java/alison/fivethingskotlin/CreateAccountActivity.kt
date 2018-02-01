package alison.fivethingskotlin

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_log_in.*

class CreateAccountActivity : AppCompatActivity() {

    lateinit var email: String
    lateinit var password1: String
    lateinit var password2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        getStartedButton.setOnClickListener{
            createAccount()
        }
    }

    private fun createAccount() {
        email = create_email.text.toString()
        password1 = create_password1.text.toString()
        password2 = create_password2.text.toString()

        if (passwordsAreSame()) {
            Log.d("blerg", "inside createAccount")
            //TODO call nagkumar's service
            val account = Account(email, "FIVE_THINGS")
            val token = "blahblah fake token" //TODO
            val accountManager = AccountManager.get(this)
            accountManager.addAccountExplicitly(account, password1, null)
            accountManager.setAuthToken(account, "full_service", token)
            val intent = Intent()
            intent.putExtra("ACCOUNT", account)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            //TODO notify user passwords dont match
        }

    }

    private fun passwordsAreSame(): Boolean {
        //TODO
        return true
    }
}
