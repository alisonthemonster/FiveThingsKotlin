package alison.fivethingskotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_promo.*

class PromoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)
    }

    override fun onStart() {
        super.onStart()
        // TODO check if user is logged in
        if (true) {
            signInButton.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            createAccountButton.setOnClickListener {
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
            }
        } else {
            Log.d("auth", "user is already logged in!")
            val intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }
    }
}
