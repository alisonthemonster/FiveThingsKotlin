package alison.fivethingskotlin

import alison.fivethingskotlin.API.RetrofitHelper
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogInActivityInstrumentedTest{

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
    fun userEntersBadEmail() {

    }

    @Test
    fun emailNotFoundByServer() {
        mockServer.enqueue(invalidCredentialsResponse)

    }

    @Test
    fun emailNeedsVerified() {
        mockServer.enqueue(inacvtivatedAccountResponse)

    }

    @Test
    fun invalidPassword() {
        mockServer.enqueue(invalidCredentialsResponse)

    }

    @Test
    fun userLogsInCorrectly() {
        mockServer.enqueue(successfulAccountLoginResponse)

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