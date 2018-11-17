package alison.fivethingskotlin

import android.app.Application
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


class FiveThingsApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Larsseit-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())

    }

}