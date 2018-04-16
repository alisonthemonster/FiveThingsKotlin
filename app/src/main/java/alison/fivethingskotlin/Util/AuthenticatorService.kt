package alison.fivethingskotlin.Util

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService : Service() {

    private var mAuthenticator: Authenticator? = null

    override fun onCreate() {
        // Create a new authenticator object
        mAuthenticator = Authenticator(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return mAuthenticator!!.iBinder
    }
}
