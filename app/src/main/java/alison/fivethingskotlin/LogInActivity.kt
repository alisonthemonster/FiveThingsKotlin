package alison.fivethingskotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val firebase = FirebaseAuth.getInstance()
        if (firebase.currentUser != null) {
            val intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }
    }
}
