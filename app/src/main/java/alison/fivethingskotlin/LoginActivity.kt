package alison.fivethingskotlin

import alison.fivethingskotlin.API.UserRepositoryImpl
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Models.Status.*
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Resource
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
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

        val userRepository = UserRepositoryImpl()

        userRepository.logIn(LogInUserRequest(email, password)).observe(this, Observer<Resource<Token>> { resource ->
            resource?.let {
                if (resource.status == SUCCESS) {
                    Log.d("blerg", "login was successful and token was passed back")
                    val authToken = resource.data?.tokenString
                    val accountManager = AccountManager.get(this)
                    val account = Account(email, "FIVE_THINGS")
                    accountManager.setAuthToken(account, "full_access", authToken)
                    val intent = Intent(this, ContainerActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    //TODO check backstack here
                    startActivity(intent)
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    Log.d("blerg", "error response found by activity")
                }

            }
        })

    }

}
