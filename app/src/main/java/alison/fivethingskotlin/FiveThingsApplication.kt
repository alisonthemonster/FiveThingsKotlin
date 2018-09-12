package alison.fivethingskotlin

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class FiveThingsApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Larsseit-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        Fabric.with(this, Crashlytics())

    }

}