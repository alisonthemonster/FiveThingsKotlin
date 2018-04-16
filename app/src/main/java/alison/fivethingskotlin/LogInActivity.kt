package alison.fivethingskotlin

import alison.fivethingskotlin.API.repository.UserRepositoryImpl
import alison.fivethingskotlin.Models.LogInUserRequest
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Constants.ACCOUNT_TYPE
import alison.fivethingskotlin.Util.Constants.AUTH_TOKEN_TYPE
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.databinding.ActivityLogInBinding
import android.accounts.Account
import android.accounts.AccountManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_log_in.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LogInActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //TODO add button to go back to promo screen

        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)

        logInButton.setOnClickListener{
            logIn()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun logIn() {
        email = login_email.text.toString().toLowerCase()
        password = login_password.text.toString()

        if (allFieldsAreFilledOut()) {
            binding.setLoading(true)

            val userRepository = UserRepositoryImpl()
            userRepository.logIn(LogInUserRequest(email, password)).observe(this, Observer<Resource<Token>> { resource ->
                resource?.let {
                    binding.setLoading(false)
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val authToken = resource.data?.token
                            val accountManager = AccountManager.get(this)
                            val account = Account(email, ACCOUNT_TYPE)
                            accountManager.addAccountExplicitly(account, password, null)
                            accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, authToken)

                            val intent = Intent(this, ContainerActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        Status.ERROR -> Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun allFieldsAreFilledOut(): Boolean {
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            true
        } else {
            Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show()
            false
        }
    }

}
