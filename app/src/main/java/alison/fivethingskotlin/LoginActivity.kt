package alison.fivethingskotlin

import alison.fivethingskotlin.API.UserRepositoryImpl
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Status.SUCCESS
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Constants.ACCOUNT_TYPE
import alison.fivethingskotlin.Util.Constants.AUTH_TOKEN_TYPE
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.databinding.ActivityLogInBinding
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_log_in.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)

//        FiveThingsApplication.component.inject(this)

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

        if (allFieldsAreFilledOut()) {
            binding.setLoading(true)

            val userRepository = UserRepositoryImpl()
            userRepository.logIn(LogInUserRequest(email, password)).observe(this, Observer<Resource<Token>> { resource ->
                resource?.let {
                    binding.setLoading(false)
                    if (resource.status == SUCCESS) {
                        Log.d("blerg", "login was successful and token was passed back")
                        val authToken = resource.data?.token
                        Log.d("blerg", "mr.token: " + authToken)
                        val accountManager = AccountManager.get(this)
                        val account = Account(email, ACCOUNT_TYPE)
                        val success = accountManager.addAccountExplicitly(account, password, null)
                        Log.d("blerg", "Account has never logged in on this device: " + success)
                        accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, authToken)
                        val intent = Intent(this, ContainerActivity::class.java)
                        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        //TODO check backstack here
                        startActivity(intent)
                        setResult(Activity.RESULT_OK)
                        finish()

                    } else {
                        //TODO find a way to get all the various error types and present them to user
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                        Log.d("blerg", "error response found by activity")
                    }

                }
            })
        }
    }

    private fun allFieldsAreFilledOut(): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            return true
        } else {
            Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}
