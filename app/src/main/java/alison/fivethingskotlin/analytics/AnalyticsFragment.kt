package alison.fivethingskotlin.analytics

import alison.fivethingskotlin.R
import alison.fivethingskotlin.databinding.FragmentAnalyticsBinding
import alison.fivethingskotlin.util.handleErrorState
import alison.fivethingskotlin.util.restoreAuthState
import alison.fivethingskotlin.util.subtractXDaysFromDate
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.fragment_analytics.*
import net.openid.appauth.AuthorizationService
import java.util.*


class AnalyticsFragment : Fragment() {

    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var binding: FragmentAnalyticsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)


        viewModel = activity?.run {
            ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeErrors()
        observeSentimentOverTime()
        observeEmotionsPieChartData()

        fetchAnalytics(Date(), subtractXDaysFromDate(Date(), 7)) //default to last seven days
        week_chip.isChecked = true

        chip_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.week_chip -> fetchAnalytics(subtractXDaysFromDate(Date(), 7), Date())
                R.id.month_chip -> fetchAnalytics(subtractXDaysFromDate(Date(), 30), Date())
                R.id.three_month_chip -> fetchAnalytics(subtractXDaysFromDate(Date(), 90), Date())
                R.id.year_chip -> fetchAnalytics(subtractXDaysFromDate(Date(), 365), Date())
            }
        }
    }

    private fun fetchAnalytics(startDate: Date, endDate: Date) {
        getSentimentOverTime(startDate, endDate)
        getEmotionPieChart(startDate, endDate)
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
    }

    private fun observeEmotionsPieChartData() {
        viewModel.getPieChartData().observe(this, Observer { data ->
            if (data == null) {
                handleErrorState("Chart data was null", context!!)
            } else {
                pie_chart.pieChartData = data
            }
        })
    }

    private fun getSentimentOverTime(startDate: Date, endDate: Date) {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getSentimentOverTime("Bearer $idToken", startDate, endDate)
            }
        }
    }

    private fun getEmotionPieChart(startDate: Date, endDate: Date) {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getEmotionsCount("Bearer $idToken", startDate, endDate)
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
