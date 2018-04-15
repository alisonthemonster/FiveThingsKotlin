package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThingz
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Util.*
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
import android.accounts.AccountManager
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.five_things_fragment.*
import java.util.*


class FiveThingsFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FiveThingsFragmentBinding
    private lateinit var yearList: MutableList<String>
    private lateinit var currentDate: Date

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //TODO handle case where user get here without logging in

        val accountManager = AccountManager.get(context)
        viewModel = FiveThingsViewModel(accountManager) //TODO switch to viewmodelprovider

        binding = FiveThingsFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        currentDate = Date()

        binding.loading = true


        viewModel.getFiveThings(Date()).observe(this, Observer<Resource<FiveThingz>> { fiveThings ->
            when (fiveThings?.status) {
                Status.SUCCESS -> {
                    binding.fiveThings = fiveThings.data
                    fiveThings.data?.let {
                        binding.naguDate = it.date
                        binding.month = getMonth(it.date) + " " + getYear(it.date)
                        compactcalendar_view.setCurrentDate(it.date)
                    }
                }
                Status.ERROR -> {
                    binding.loading = false
                    Toast.makeText(context, fiveThings.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

//        //Pull in today's data
//        viewModel.getDate().observe(this, Observer<Date> { date ->
//            //TODO can this info come from the above call?
//            binding.loading = false
//            val currDate = date ?: Date()
//            Log.d("blerg", "currdate: " + currDate)
//            binding.naguDate = currDate
//            binding.month = getMonth(currDate) + " " + getYear(currDate)
//            compactcalendar_view.setCurrentDate(currDate)
//        })

        //build calendar when days come back from server
        viewModel.getWrittenDays().observe(this, Observer<Resource<List<Date>>> { days ->
            binding.loading = false
            days?.let{
                when (it.status) {
                    Status.SUCCESS -> addEventsToCalendar(it.data)
                    Status.ERROR ->Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.calendarVisible = false

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                viewModel.changeDate(dateClicked)
                binding.calendarVisible = false
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                currentDate = firstDayOfNewMonth //TODO what does currentDate do again?
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
                Log.d("blerg", "in the observer")
                when (it?.status) {
                    Status.SUCCESS -> addEventsToCalendar(it.data)
                    Status.ERROR -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun addEventsToCalendar(dates: List<Date>?) {
        Log.d("blerg", "updating calendar")
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
                        .setItems(yearList.toTypedArray(), { _, year ->
                            val newDate = getDateInAYear(currentDate, yearList[year].toInt())
                            currentDate = newDate
                            binding.month = getMonth(newDate) + " " + getYear(newDate)
                            compactcalendar_view.setCurrentDate(newDate)
                        })
                        .create()
                        .show()
            }
        }
    }

    //TODO handle when user tries to leave fragment with un-saved changes
}
