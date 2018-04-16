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
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_account.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.regex.Pattern


class CreateAccountActivity : AppCompatActivity() {

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password1: String
    private lateinit var password2: String
    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)

        getStartedButton.setOnClickListener{
            createAccount()
        }

        alreadyHaveAccountText.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount() {
        name = create_name.text.toString()
        email = create_email.text.toString().toLowerCase()
        password1 = create_password1.text.toString()
        password2 = create_password2.text.toString()

        if (allFieldsComplete() && passwordsAreAllGood() && emailIsValid()) {
            binding.setLoading(true)

            val userRepository = UserRepositoryImpl()
            userRepository.createUser(CreateUserRequest(name, password1, email)).observe(this, Observer<Resource<Token>> { tokenResource ->
                tokenResource?.let {
                    binding.setLoading(false)
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

    private fun passwordsAreAllGood(): Boolean {
        return if (password1 == password2) {
            if (password1.length < 6) {
                Toast.makeText(this, "Password needs to be at least 6 characters", Toast.LENGTH_SHORT).show()
                false
            } else
                true
        } else {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun emailIsValid(): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pat = Pattern.compile(emailRegex)
        return if (pat.matcher(email).matches()) {
            true
        } else {
            Toast.makeText(this, "Not a valid email", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun allFieldsComplete(): Boolean {
        if (name.isNotEmpty() && password1.isNotEmpty() && password2.isNotEmpty() && email.isNotEmpty()) {
            return true
        } else {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}
