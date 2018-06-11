package alison.fivethingskotlin

import alison.fivethingskotlin.API.RetrofitHelper
import alison.fivethingskotlin.util.withCustomError
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CreateAccountActivityInstrumentedTests {

    @Rule
    @JvmField
    val mActivityRule: ActivityTestRule<CreateAccountActivity> = ActivityTestRule(CreateAccountActivity::class.java)

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
    fun checkEmailIsValid() {
        onView(withId(R.id.email_text)).perform(typeText("bad@email"))
        onView(withId(R.id.input_email)).check(matches(withCustomError(`is`("Please enter a valid email address"))))
    }

    @Test
    fun checkPasswordIsAtLeastSixChars() {
        onView(withId(R.id.password1_text)).perform(typeText("12345"))
        onView(withId(R.id.input_password1)).check(matches(withCustomError(`is`("Passwords must be at least six characters"))))
    }

    @Test
    fun checkPasswordsMatch() {
        onView(withId(R.id.password1_text)).perform(typeText("password"))
        onView(withId(R.id.input_password2)).perform(scrollTo())
        onView(withId(R.id.password2_text)).perform(typeText("password1"))
        onView(withId(R.id.input_password2)).check(matches(withCustomError(`is`("Passwords must match"))))
    }

    @Test
    fun createAnAccountSuccessfully() {
        mockServer.enqueue(successfulAccountCreationResponse)

        onView(withId(R.id.name_text)).perform(typeText("Test User"))
        onView(withId(R.id.email_text)).perform(typeText("test@test.com"))
        onView(withId(R.id.password1_text)).perform(typeText("password"))
        onView(withId(R.id.input_password2)).perform(scrollTo())
        onView(withId(R.id.password2_text)).perform(typeText("password"))
        onView(withId(R.id.getStartedButton)).perform(scrollTo())
        onView(withId(R.id.getStartedButton)).perform(click())

        onView(withId(R.id.login_header)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun createAnAccountWithExistingEmail() {
        mockServer.enqueue(accountExistsResponse)

        onView(withId(R.id.name_text)).perform(typeText("Test User"))
        onView(withId(R.id.email_text)).perform(typeText("test@test.com"))
        onView(withId(R.id.password1_text)).perform(typeText("password"))
        onView(withId(R.id.input_password2)).perform(scrollTo())
        onView(withId(R.id.password2_text)).perform(typeText("password"))
        onView(withId(R.id.getStartedButton)).perform(scrollTo())
        onView(withId(R.id.getStartedButton)).perform(click())

        onView(withText(R.string.already_exists)).inRoot(withDecorView(not(`is`(mActivityRule.activity.window.decorView)))).check(matches(isDisplayed()))
    }

    private val successfulAccountCreationResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\n" +
                    "    \"message\": \"User created. Check email to activate account\"\n" +
                    "}")


    private val accountExistsResponse = MockResponse()
            .setResponseCode(400)
            .setBody("{\n" +
                    "    \"message\": \"Email ID exists\"\n" +
                    "}")
}