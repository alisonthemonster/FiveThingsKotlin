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
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_log_in.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.regex.Pattern


class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //TODO add button to go back to promo screen

        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)

        input_email.hint = "Email Address"
        input_password.hint = "Password"

        //calligraphy hack for password fields
        password_text.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        password_text.transformationMethod = PasswordTransformationMethod.getInstance()

        setUpListeners()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun logInClick(view: View) {
        logIn()
    }

    private fun logIn() {
        val email = email_text.text.toString().toLowerCase()
        val password = password_text.text.toString()

        //TODO dismiss keyboard

        if (validateEmail(email) && validatePassword(password)) {
            binding.loading = true

            val userRepository = UserRepositoryImpl()
            userRepository.logIn(LogInUserRequest(email, password)).observe(this, Observer<Resource<Token>> { resource ->
                resource?.let {
                    binding.loading = false
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

    private fun validateEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pat = Pattern.compile(emailRegex)
        return if (pat.matcher(email).matches()) {
            input_email.isErrorEnabled = false
            true
        } else {
            input_email.error = "Please enter a valid email address"
            false
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isNotEmpty()) {
            input_password.isErrorEnabled = false
            true
        } else {
            input_password.error = "Please enter a password"
            false
        }
    }

    private fun setUpListeners() {
        email_text.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                validateEmail(email_text.text.toString())
            }
        })

        password_text.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                validatePassword(password_text.text.toString())
            }
        })
        password_text.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                logIn()
                handled = true
            }
            handled
        }
    }
}
