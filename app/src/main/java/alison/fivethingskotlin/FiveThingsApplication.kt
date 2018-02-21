package alison.fivethingskotlin

import alison.fivethingskotlin.dagger.AppComponent
import alison.fivethingskotlin.dagger.AppModule
import alison.fivethingskotlin.dagger.DaggerAppComponent
import android.app.Application
import android.content.Context
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

//        component.provideContext()
    }

    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

}