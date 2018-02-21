package alison.fivethingskotlin


import android.app.Application
import android.util.Log
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class FiveThingsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("blerg", "injecting the context into the component")

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Larsseit-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}