package alison.fivethingskotlin.Util

import android.content.Context
import android.text.TextUtils
import net.openid.appauth.AuthState
import org.json.JSONException

const val SHARED_PREFERENCES_NAME = "AuthStatePreference"
const val AUTH_STATE = "AUTH_STATE"

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

fun clearAuthState(context: Context) {
    context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(AUTH_STATE)
            .apply()
}