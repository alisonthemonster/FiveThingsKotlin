package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.databinding.FragmentCalendarBinding
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.util.*
import alison.fivethingskotlin.viewmodel.FiveThingsViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import net.openid.appauth.AuthorizationService
import java.lang.Exception
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(FiveThingsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.closeCalendarEvent.observe(this, android.arch.lifecycle.Observer {

            fragmentManager?.popBackStack()
        })


        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }
        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getDays("Bearer $idToken").observe(this, android.arch.lifecycle.Observer { dates ->
                    when (dates?.status) {
                        Status.SUCCESS -> {
                            addEventsToCalendar(dates.data)
                        }
                        Status.ERROR -> {
                            val message = dates.message!!.capitalize()
                            handleErrorState(message, context!!)
                        }
                    }
                })
            }
        }

        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val activity = context as ContainerActivity
                activity.selectDate(dateClicked, false)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                viewModel.month.set(getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth))
            }
        })

        todayButton.setOnClickListener {
            val activity = context as ContainerActivity
            activity.selectDate(Date(), false)
        }

    }

    private fun addEventsToCalendar(dates: List<Date>?) {
        compactcalendar_view.removeAllEvents()
        dates?.let {
            val events = it.map { convertDateToEvent(it) }
            compactcalendar_view.addEvents(events)
            if (it.isNotEmpty()) {
                buildYearDialog(it)
            }
        }
    }

    private fun buildYearDialog(dates: List<Date>) {
        val yearList = mutableListOf<String>()
        val minYear = getYear(Collections.min(dates))
        val maxYear = getYear(Collections.max(dates))
        (minYear..maxYear).mapTo(yearList) { it.toString() }

        if (yearList.size > 1) {
            //only show dialog if users have multiple years to choose from
            month_year.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
                dialogBuilder
                        .setTitle("Select a year")
                        .setItems(yearList.toTypedArray()) { _, year ->
                            //TODO
//                            val newDate = getDateInAYear(currentDate, yearList[year].toInt())
//                            currentDate = newDate
//                            binding.month = getMonth(newDate) + " " + getYear(newDate)
//                            compactcalendar_view.setCurrentDate(newDate)
                        }
                        .create()
                        .show()
            }
        }
    }


    companion object {
        const val DATE = "date_key"

        val TAG = CalendarFragment::class.java.simpleName

        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }


}