package alison.fivethingskotlin.util

import com.google.android.material.textfield.TextInputLayout
import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import java.lang.reflect.InvocationTargetException


fun withCustomHint(stringMatcher: Matcher<String>): Matcher<View> {
    return object : BaseMatcher<View>() {
        override fun describeTo(description: Description) {}

        override fun matches(item: Any): Boolean {
            try {
                val method = item.javaClass.getMethod("getHint")
                return stringMatcher.matches(method.invoke(item))
            } catch (e: NoSuchMethodException) {
            } catch (e: InvocationTargetException) {
            } catch (e: IllegalAccessException) {
            }

            return false
        }
    }
}

fun withCustomError(stringMatcher: Matcher<String>): Matcher<View> {
    return object : BaseMatcher<View>() {
        override fun describeTo(description: Description) {}

        override fun matches(item: Any): Boolean {
            try {
                val method = item.javaClass.getMethod("getError")
                return stringMatcher.matches(method.invoke(item))
            } catch (e: NoSuchMethodException) {
            } catch (e: InvocationTargetException) {
            } catch (e: IllegalAccessException) {
            }

            return false
        }
    }
}