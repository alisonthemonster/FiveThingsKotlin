package alison.fivethingskotlin.analytics

import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.handleErrorState
import alison.fivethingskotlin.util.restoreAuthState
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.fragment_analytics.*
import net.openid.appauth.AuthorizationService
import java.util.*
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import android.graphics.Typeface




class AnalyticsFragment : Fragment() {

    private lateinit var viewModel: AnalyticsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")


        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeErrors()
        observeSentimentOverTime()
        getSentimentOverTime()
        getMpSentimentOverTime()
    }

    private fun observeSentimentOverTime() {
        viewModel.getChartData().observe(this, Observer { data ->
            if (data == null) {
                handleErrorState("Chart data was null", context!!)
            } else {
                data.lines[0].pointRadius = 0 //hide points
                val font = Typeface.createFromAsset(activity?.assets, "fonts/Larsseit.ttf")
                data.axisXBottom.typeface = font
                chart.lineChartData = data

                chart.isInteractive = false

                val viewport = chart.maximumViewport
                viewport.set(viewport.left, 1f, viewport.right, -1f)
                chart.currentViewport = viewport
                chart.maximumViewport = viewport
            }
        })

        viewModel.getMpChartData().observe(this, Observer { data ->
            if (data == null) {
                handleErrorState("Chart data was null", context!!)
            } else {
                mp_chart.data = data
                mp_chart.xAxis.setDrawGridLines(false)
                mp_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                mp_chart.axisLeft.axisMaximum = 1f
                mp_chart.axisLeft.axisMinimum = -1f
                mp_chart.axisLeft.setDrawGridLines(false)
                mp_chart.axisRight.isEnabled = false
                mp_chart.axisLeft.setDrawLabels(false)
                mp_chart.axisLeft.setDrawAxisLine(false)
                mp_chart.setDrawBorders(false)
                mp_chart.xAxis.textColor = Color.WHITE

                mp_chart.legend.isEnabled = false

                mp_chart.isHighlightPerDragEnabled = false
                mp_chart.isHighlightPerTapEnabled = false
                mp_chart.setScaleEnabled(false)

                mp_chart.setNoDataText("Insufficient data for analysis")

                val zeroAxis = LimitLine(0f)
                zeroAxis.lineColor = Color.WHITE
                zeroAxis.enableDashedLine(4f, 10f, 0f)
                zeroAxis.lineWidth = 1f
                mp_chart.axisLeft.addLimitLine(zeroAxis)

                mp_chart.invalidate()
            }
        })
    }

    private fun getSentimentOverTime() {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getSentimentOverTime("Bearer $idToken", Date(), Date())
            }
        }
    }

    private fun getMpSentimentOverTime() {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getMPSentimentOverTime("Bearer $idToken", Date(), Date())
            }
        }
    }
    private fun observeErrors() {
        viewModel.errorLiveEvent.observe(this, Observer {
            Crashlytics.logException(Exception("Message: ${it?.capitalize()}"))
            handleErrorState(it ?: "Unknown Error", context!!)
        })
    }
}
