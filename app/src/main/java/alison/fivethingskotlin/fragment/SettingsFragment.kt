package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.BuildConfig
import alison.fivethingskotlin.R
import alison.fivethingskotlin.WebViewActivity
import alison.fivethingskotlin.WebViewActivity.Companion.WEBVIEW_URL
import alison.fivethingskotlin.util.NotificationScheduler
import alison.fivethingskotlin.util.TimePreference
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.analytics.FirebaseAnalytics


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_preferences)

        buildWebViewPreference("privacy_policy", BuildConfig.PRIVACY_POLICY_URL)
        buildWebViewPreference("terms_conditions", BuildConfig.TERMS_CONDITIONS_URL)
        buildOssLiscenceActivity()

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
    }

    private fun buildWebViewPreference(preferenceKey: String, webViewUrl: String) {
        val preference = findPreference(preferenceKey)
        preference.setOnPreferenceClickListener {
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra(WEBVIEW_URL, webViewUrl)
            }
            startActivity(intent)
            true
        }
    }

    private fun buildOssLiscenceActivity() {
        val preference = findPreference("open_source")
        preference.setOnPreferenceClickListener {
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            true
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: androidx.fragment.app.DialogFragment? = null
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceFragment()
            val bundle = Bundle(1)
            bundle.putString("key", preference.getKey())
            dialogFragment.setArguments(bundle)
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.fragmentManager!!, "BLERG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        when(key) {
            "notif_parent" -> {
                val scheduler = NotificationScheduler()
                val allowNotifs = prefs.getBoolean("notif_parent", true)
                if (!allowNotifs) {
                    scheduler.cancelNotifications(context!!)
                } else {
                    scheduler.setReminderNotification(context!!)
                }
            }
            "dark_light_mode" -> {
                val isLightMode = prefs.getBoolean("dark_light_mode", false) //default is dark mode
                if (isLightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //LIGHT MODE

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) //DARK MODE
                }
                activity?.recreate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics.setCurrentScreen(activity as Activity, "SettingsScreen", null)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

}
