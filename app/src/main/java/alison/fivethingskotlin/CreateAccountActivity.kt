package alison.fivethingskotlin

import alison.fivethingskotlin.API.repository.UserRepositoryImpl
import alison.fivethingskotlin.Models.CreateUserRequest
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Constants.ACCOUNT_TYPE
import alison.fivethingskotlin.Util.Constants.AUTH_TOKEN_TYPE
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.databinding.ActivityCreateAccountBinding
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.PendingIntent.getActivity
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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_account.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.regex.Pattern


class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)

        input_name.hint = "Name"
        input_email.hint = "Email Address"
        input_password1.hint = "Password"
        input_password2.hint = "Password"

        //calligraphy hack for password fields
        password1_text.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        password1_text.transformationMethod = PasswordTransformationMethod.getInstance()
        password2_text.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        password2_text.transformationMethod = PasswordTransformationMethod.getInstance()

        setUpListeners()

        getStartedButton.setOnClickListener{
            createAccount()
        }

        alreadyHaveAccountText.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount() {
        val name = name_text.text.toString()
        val email = email_text.text.toString().toLowerCase()
        val password1 = password1_text.text.toString()
        val password2 = password2_text.text.toString()

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        if (validateName(name) &&
                validateEmail(email) &&
                validatePassword(password1) &&
                validateRepeatPassword(password1, password2)) {

            binding.loading = true

            val userRepository = UserRepositoryImpl()
            userRepository.createUser(CreateUserRequest(name, password1, email)).observe(this, Observer<Resource<Token>> { tokenResource ->
                tokenResource?.let {
                    binding.loading = false
                    if (tokenResource.status == Status.SUCCESS) {
                        val account = Account(email, ACCOUNT_TYPE)
                        val accountManager = AccountManager.get(this)
                        accountManager.addAccountExplicitly(account, password1, null)
                        accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, tokenResource.data?.token)

                        //Send user back to login screen so they can log in after validating email
                        val intent = Intent(this, LogInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, tokenResource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun validateName(name: String): Boolean {
        return if (name.isNotEmpty()) {
            input_name.isErrorEnabled = false
            true
        } else {
            input_name.error = "Please enter your name"
            false
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
        return if (password.length >= 6) {
            input_password1.isErrorEnabled = false
            true
        } else {
            input_password1.error = "Passwords must be at least six characters"
            false
        }
    }

    private fun validateRepeatPassword(password1: String, password2: String): Boolean {
        return if (password1 == password2) {
            input_password2.isErrorEnabled = false
            true
        } else {
            input_password2.error = "Passwords must match"
            false
        }
    }

    private fun setUpListeners() {
        name_text.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                validateName(name_text.text.toString())
            }
        })

        email_text.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                validateEmail(email_text.text.toString())
            }
        })

        password1_text.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                validatePassword(password1_text.text.toString())
            }
        })

        password2_text.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                validateRepeatPassword(password1_text.text.toString(), password2_text.text.toString())
            }
        })

        password2_text.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                createAccount()
                handled = true
            }
            handled
        }
    }
}
