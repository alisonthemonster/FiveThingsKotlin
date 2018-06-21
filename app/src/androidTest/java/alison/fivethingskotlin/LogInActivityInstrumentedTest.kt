package alison.fivethingskotlin

import alison.fivethingskotlin.API.RetrofitHelper
import alison.fivethingskotlin.util.withCustomError
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogInActivityInstrumentedTest{

    @Rule
    @JvmField
    val mActivityRule: ActivityTestRule<LogInActivity> = ActivityTestRule(LogInActivity::class.java)

    private var mockServer = MockWebServer()

    @Before
    fun setUp() {
        mockServer.start()
        RetrofitHelper.baseUrl = mockServer.url("/").toString()
    }

    @After
    fun cleanUp() {
        mockServer.shutdown()
    }

    @Test
    fun userEntersBadEmail() {
        Espresso.onView(ViewMatchers.withId(R.id.email_text)).perform(ViewActions.typeText("bad@email"))
        Espresso.onView(ViewMatchers.withId(R.id.input_email)).check(ViewAssertions.matches(withCustomError(Matchers.`is`("Please enter a valid email address"))))
    }

    @Test
    fun emailNotFoundByServer() {
        mockServer.enqueue(invalidCredentialsResponse)

        Espresso.onView(ViewMatchers.withId(R.id.email_text)).perform(ViewActions.typeText("email@email.com"))
        Espresso.onView(ViewMatchers.withId(R.id.password_text)).perform(ViewActions.typeText("password"))
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Invalid credentials.")).inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(mActivityRule.activity.window.decorView)))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emailNeedsVerified() {
        mockServer.enqueue(inacvtivatedAccountResponse)

        Espresso.onView(ViewMatchers.withId(R.id.email_text)).perform(ViewActions.typeText("unverfied@email.com"))
        Espresso.onView(ViewMatchers.withId(R.id.password_text)).perform(ViewActions.typeText("password"))
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Account needs activation!")).inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(mActivityRule.activity.window.decorView)))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun invalidPassword() {
        mockServer.enqueue(invalidCredentialsResponse)

        Espresso.onView(ViewMatchers.withId(R.id.email_text)).perform(ViewActions.typeText("email@email.com"))
        Espresso.onView(ViewMatchers.withId(R.id.password_text)).perform(ViewActions.typeText("wrongPassword"))
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Invalid credentials.")).inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(mActivityRule.activity.window.decorView)))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun userLogsInCorrectly() {
        mockServer.enqueue(successfulAccountLoginResponse)

        Espresso.onView(ViewMatchers.withId(R.id.email_text)).perform(ViewActions.typeText("email@email.com"))
        Espresso.onView(ViewMatchers.withId(R.id.password_text)).perform(ViewActions.typeText("password"))
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.content_frame)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }

    private val successfulAccountLoginResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\n" +
                    "    \"token\": \"bleep-bloop-bleep-token\"\n" +
                    "}")


    private val invalidCredentialsResponse = MockResponse()
            .setResponseCode(401)
            .setBody("Invalid credentials.")

    private val inacvtivatedAccountResponse = MockResponse()
            .setResponseCode(401)
            .setBody("Account needs activation!")
}