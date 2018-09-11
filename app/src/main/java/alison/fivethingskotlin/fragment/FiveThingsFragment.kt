package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.api.repository.FiveThingsRepositoryImpl
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.util.*
import alison.fivethingskotlin.viewmodel.FiveThingsViewModel
import alison.fivethingskotlin.viewmodel.FiveThingsViewModelFactory
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
import alison.fivethingskotlin.model.Resource
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.designs_fragment.*
import kotlinx.android.synthetic.main.five_things_fragment.*
import net.openid.appauth.AuthorizationService
import org.joda.time.Days
import org.joda.time.LocalDate
import java.util.*


class FiveThingsFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FiveThingsFragmentBinding
    private lateinit var yearList: MutableList<String>
    private lateinit var currentDate: Date

    companion object {

        const val DATE = "date_key"

        fun newInstance(date: String): FiveThingsFragment {
            val fragment = FiveThingsFragment()

            val bundle = Bundle()
            bundle.putString(DATE, date)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FiveThingsFragmentBinding.inflate(inflater, container, false)
        binding.loading = true

        context?.let {
            val authorizationService = AuthorizationService(it)
            val authState = restoreAuthState(it)

            viewModel = ViewModelProviders.of(this,
                    FiveThingsViewModelFactory(FiveThingsRepositoryImpl(), authState, authorizationService))
                    .get(FiveThingsViewModel::class.java)

            binding.viewModel = viewModel

            val passedInDate = arguments?.getString(DATE)

            currentDate = if (passedInDate != null)
                getDateFromFullDateFormat(passedInDate) else Date()

            getFiveThings()

            getWrittenDays()
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()

        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
//                currentDate = dateClicked
//                viewModel.changeDate(dateClicked)
//                binding.calendarVisible = false
                val activity = context as ContainerActivity
                activity.onDateSelected(currentDate, dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                currentDate = firstDayOfNewMonth
                binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
            }
        })

        current_date.setOnClickListener {
            val currentVisibility = binding.calendarVisible
            currentVisibility?.let {
                binding.calendarVisible = !currentVisibility
            }
        }

        save_button.setOnClickListener {
            viewModel.writeFiveThings(binding.fiveThings!!).observe(this, Observer<Resource<List<Date>>> {
                when (it?.status) {
                    Status.SUCCESS -> addEventsToCalendar(it.data)
                    Status.ERROR -> showErrorDialog(it.message!!.capitalize(), context!!)
                }

            })
        }
    }

    private fun getFiveThings() {

        binding.loading = true

        viewModel.getFiveThings(currentDate).observe(this, Observer<Resource<FiveThings>> { fiveThings ->
            when (fiveThings?.status) {
                Status.SUCCESS -> {
                    binding.fiveThings = fiveThings.data
                    fiveThings.data?.let {
                        binding.naguDate = it.date
                        binding.month = getMonth(it.date) + " " + getYear(it.date)
                        compactcalendar_view.setCurrentDate(it.date)
                        binding.loading = false
                    }
                }
                Status.ERROR -> {
                    binding.loading = false
                    showErrorDialog(fiveThings.message!!.capitalize(), context!!)
                    //Toast.makeText(context, fiveThings.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getWrittenDays() {
        //build calendar when days come back from server
        viewModel.getWrittenDays().observe(this, Observer<Resource<List<Date>>> { days ->
            days?.let{
                when (it.status) {
                    Status.SUCCESS -> addEventsToCalendar(it.data)
                    Status.ERROR -> showErrorDialog(it.message!!.capitalize(), context!!)
                }
            }
        })

        binding.calendarVisible = false
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
        binding.loading = false
    }

    private fun buildYearDialog(dates: List<Date>) {
        yearList = mutableListOf()
        val minYear = getYear(Collections.min(dates))
        val maxYear = getYear(Collections.max(dates))
        (minYear..maxYear).mapTo(yearList) { it.toString() }

        if (yearList.size > 1) {
            //only show dialog if users have multiple years to choose from
            month_year.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder
                        .setTitle("Select a year")
                        .setItems(yearList.toTypedArray()) { _, year ->
                            val newDate = getDateInAYear(currentDate, yearList[year].toInt())
                            currentDate = newDate
                            binding.month = getMonth(newDate) + " " + getYear(newDate)
                            compactcalendar_view.setCurrentDate(newDate)
                        }
                        .create()
                        .show()
            }
        }
    }

    //TODO handle when user tries to leave fragment with un-inDatabase changes
}
