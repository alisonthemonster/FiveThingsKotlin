package alison.fivethingskotlin.analytics

import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.*
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import kotlinx.android.synthetic.main.fragment_analytics.*
import net.openid.appauth.AuthorizationService
import java.util.*
import lecho.lib.hellocharts.model.*
import alison.fivethingskotlin.R.id.chart
import alison.fivethingskotlin.R.id.start


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
    }


    private fun setUpSentimentOverTimeGraph() {
        // set date label formatter
        sentiment_over_time_graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(activity)

        sentiment_over_time_graph.viewport.isXAxisBoundsManual = true
        sentiment_over_time_graph.gridLabelRenderer.setHumanRounding(false)
        sentiment_over_time_graph.viewport.setMaxY(1.0)
        sentiment_over_time_graph.viewport.setMinY(-1.0)
    }

    private fun observeSentimentOverTime() {
        viewModel.getSentimentDataPoints().observe(this, Observer {
            //            sentiment_over_time_graph.addSeries(it)
//            sentiment_over_time_graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(context)
//            sentiment_over_time_graph.viewport.setMaxY(1.0)
//            sentiment_over_time_graph.viewport.setMinY(-1.0)
//            sentiment_over_time_graph.viewport.isXAxisBoundsManual = true
//            sentiment_over_time_graph.gridLabelRenderer.numHorizontalLabels = 3


            //get max and min dates
            //LAST 7 DAYS: if dates are equal to seven days apart
            //for each value write the day of the week
            //LAST 30 DAYS: if dates are 30 days apart
            //for every five days write a label
            //LAST 90 DAYS:
            //mark every month change (detect a change and add a label
            //LAST 180 DAYS:
            //mark every month change
            //LAST 365 DAYS:
            //mark every month change


            val line = Line(it).setColor(Color.BLUE).setCubic(true)
            val lines = ArrayList<Line>()
            lines.add(line)


            val data = LineChartData()
            data.lines = lines

            val startDate = Date(it!![0].x.toLong())
            val endDate = Date(it[it.size-1].x.toLong())

            val axis = if (getDaysBetween(startDate, endDate) == 7) {
                getAxisForAWeek(it)
            } else if (getDaysBetween(startDate, endDate) == 30) {
                getAxisForLastThirtyDays(it)
            } else {
                Axis()

            }
            axis.name = "Date"
            data.axisXBottom = axis



            chart.lineChartData = data
            chart.isInteractive = false

            val viewport = chart.maximumViewport
            viewport.set(viewport.left, 1f, viewport.right, -1f)
            chart.currentViewport = viewport
            chart.maximumViewport = viewport

        })

    }

    private fun getAxisForAWeek(days: ArrayList<PointValue>): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        for (day in days) {
            val date = Date(day.x.toLong())
            val axisValue = AxisValue(day.x)
            axisValue.setLabel(getDayOfWeek(date))
            values.add(axisValue)
        }
        axis.values = values
        return axis
    }

    private fun getAxisForLastThirtyDays(days: ArrayList<PointValue>): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        for (day in days) {
            val date = Date(day.x.toLong())
            val axisValue = AxisValue(day.x)
            axisValue.setLabel("${getMonthNumber(date)}/${getDay(date)}")
            values.add(axisValue)
        }
        axis.values = values
        return axis
    }

    private fun getAxisForLastSixtyDays(days: ArrayList<PointValue>): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        for ((index, day) in days.withIndex()) {
            if (index % 5 == 0) {
                val date = Date(day.x.toLong())
                val axisValue = AxisValue(day.x)
                axisValue.setLabel("${getMonthNumber(date)}/${getDay(date)}")
                values.add(axisValue)
            }
        }
        axis.values = values
        return axis
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

    private fun observeErrors() {
        viewModel.errorLiveEvent.observe(this, Observer {
            Crashlytics.logException(Exception("Message: ${it?.capitalize()}"))
            handleErrorState(it ?: "Unknown Error", context!!)
        })
    }
}
