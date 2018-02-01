package alison.fivethingskotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        getStartedButton.setOnClickListener{
            createAccount()
        }
    }

    private fun createAccount() {
        //TODO call nagkumar's service
    }
}
