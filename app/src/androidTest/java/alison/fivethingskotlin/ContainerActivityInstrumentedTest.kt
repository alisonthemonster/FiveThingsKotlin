package alison.fivethingskotlin

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.DrawerMatchers
import android.support.test.espresso.contrib.NavigationViewActions
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import android.support.test.rule.ActivityTestRule
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class ContainerActivityInstrumentedTestInstrumentedTest {

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<ContainerActivity> = ActivityTestRule(ContainerActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("alison.fivethingskotlin", appContext.packageName)
    }

    @Test
    fun userCanOpenAppDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isOpen()))
    }

    @Test
    fun userCanClickFiveThingsNavButton() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isOpen()))
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.five_things_item))
        onView(withId(R.id.five_things_container)).check(matches(isDisplayed()))
    }

    @Test
    fun userCanClickDesignsNavButton() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isOpen()))
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.templates_item))
        onView(withId(R.id.designs_container)).check(matches(isDisplayed()))
    }

    @Test
    fun userCanClickSettingsNavButton() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isOpen()))
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.settings_item))
        onView(withId(R.id.settings_container)).check(matches(isDisplayed()))
    }

    @Test
    fun userCanClickAnalyticsNavButton() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isOpen()))
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.analytics_item))
        onView(withId(R.id.analytics_container)).check(matches(isDisplayed()))
    }

}
