package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.NotificationScheduler
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import alison.fivethingskotlin.util.TimePreference
import android.content.SharedPreferences
import android.support.v4.app.DialogFragment
import android.support.v7.preference.Preference
import android.util.Log


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_preferences)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: DialogFragment? = null
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceFragment()
            val bundle = Bundle(1)
            bundle.putString("key", preference.getKey())
            dialogFragment.setArguments(bundle)
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.fragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        Log.d("blerg", "in onchanged")
        val scheduler = NotificationScheduler()
        val allowNotifs = prefs.getBoolean("notif_parent", true)
        if (!allowNotifs) {
            Log.d("blerg", "cancelling")
            scheduler.cancelNotifications(context!!)
        } else {
            Log.d("blerg", "setting")
            scheduler.setReminderNotification(context!!)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

}
