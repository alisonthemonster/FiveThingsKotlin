package alison.fivethingskotlin.Util

import android.content.Context
import android.text.TextUtils
import net.openid.appauth.AuthState
import org.json.JSONException

//TODO convert to constants
private val SHARED_PREFERENCES_NAME = "AuthStatePreference"
private val AUTH_STATE = "AUTH_STATE"

fun restoreAuthState(context: Context): AuthState? {
    val jsonString = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(AUTH_STATE, null)
    if (!TextUtils.isEmpty(jsonString)) {
        try {
            return AuthState.fromJson(jsonString!!)
        } catch (jsonException: JSONException) {
            // should never happen
        }
    }
    return null
}