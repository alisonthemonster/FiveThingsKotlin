package alison.fivethingskotlin.adapter

import android.support.annotation.IntDef
import android.view.View

@IntDef(View.VISIBLE, View.GONE, View.INVISIBLE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Visibility