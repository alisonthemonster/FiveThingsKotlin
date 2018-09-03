package alison.fivethingskotlin

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES
import android.support.v7.preference.PreferenceManager
import android.util.Log
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class FiveThingsApplication : Application() {

    override fun onCreate() {

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val isLightMode = sharedPref.getBoolean("dark_light_mode", false) //default is dark mode

        if (isLightMode) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO) //LIGHT MODE
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES) //DARK MODE
        }

        super.onCreate()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Larsseit-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

    }

}