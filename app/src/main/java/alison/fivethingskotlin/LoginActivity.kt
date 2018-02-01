package alison.fivethingskotlin

import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log_in.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LoginActivity : AppCompatActivity() {

    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        logInButton.setOnClickListener{
            logIn()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun logIn() {
        email = login_email.text.toString()
        password = login_password.text.toString()
        //TODO
        //call nagkumar's service with username and password
    }

    private fun finishLogin() {
        val accountManager = AccountManager.get(this)
//        val acctName = intent.getString(AccountManager.KEY_ACCOUNT_NAME)
//        accountManager.setPassword(password)
    }

}
