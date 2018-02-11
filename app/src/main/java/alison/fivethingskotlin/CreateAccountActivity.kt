package alison.fivethingskotlin

import alison.fivethingskotlin.API.UserRepositoryImpl
import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.Status
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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_account.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import android.R.attr.data
import android.arch.lifecycle.LiveData




class CreateAccountActivity : AppCompatActivity() {

    lateinit var name: String
    lateinit var email: String
    lateinit var password1: String
    lateinit var password2: String

    //TODO font not working in this activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)




        getStartedButton.setOnClickListener{
            createAccount()
        }

        alreadyHaveAccountText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount() {
        name = create_name.text.toString()
        email = create_email.text.toString()
        password1 = create_password1.text.toString()
        password2 = create_password2.text.toString()

        //TODO change appearance of disabled button
        //TODO add loading indicator

        Log.d("blerg", "inside createAccount")

        if (passwordsAreSame()) {
            val userRepository = UserRepositoryImpl()
            //binding.loading = true

            userRepository.createUser(CreateUserRequest(name, password1, email)).observe(this, Observer<Resource<Token>> { resource ->
                resource?.let {
                    //binding.loading = false
                    if (resource.status == Status.SUCCESS) {
                        Log.d("blerg", "token created by nagkumar and passed back successfully")
                        val account = Account(email, "FIVE_THINGS")
                        val accountManager = AccountManager.get(this)
                        accountManager.addAccountExplicitly(account, password1, null)
                        accountManager.setAuthToken(account, "full_service", resource.data?.tokenString)
                        //accountManager.setPassword(account, refreshToken) //TODO get refresh token
                        val intent = Intent()
                        intent.putExtra("ACCOUNT", account)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        //TODO present user with error
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                        Log.d("blerg", "error response found by activity")
                    }
                }
            })
        } else {
            //TODO notify user passwords dont match
        }

    }

    private fun passwordsAreSame(): Boolean {
        //TODO
        //TODO are there minimums for password length?
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun checkValidation() {
        getStartedButton.isEnabled = !(name.isEmpty() || password1.isEmpty() || password2.isEmpty() || email.isEmpty())
    }

    var watcher: TextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            checkValidation()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {

        }

        override fun afterTextChanged(s: Editable) {
        }
    }
}
