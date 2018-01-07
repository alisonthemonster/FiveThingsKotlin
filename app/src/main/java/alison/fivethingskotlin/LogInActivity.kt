package alison.fivethingskotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class LogInActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebase: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        firebase = FirebaseAuth.getInstance()

        val logInButton = findViewById<Button>(R.id.logInButton)
        logInButton.setOnClickListener{
            logIn()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
         if (firebase.currentUser != null) {
             Log.d("auth", "user is already logged in!")
            val intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("auth", e.toString())
                Toast.makeText(this, "Uh oh, Google sign in failed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        showLoading()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebase.signInWithCredential(credential)
                .addOnCompleteListener(this, { task ->
                    hideLoading()
                    if (task.isSuccessful) {
                        val intent = Intent(this, ContainerActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun logIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun showLoading() {
        val loadingView = findViewById<ConstraintLayout>(R.id.loadingLayout)
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        val loadingView = findViewById<ConstraintLayout>(R.id.loadingLayout)
        loadingView.visibility = View.GONE
    }
}
