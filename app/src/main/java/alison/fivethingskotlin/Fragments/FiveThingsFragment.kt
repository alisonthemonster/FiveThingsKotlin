package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThingz
import alison.fivethingskotlin.R
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.five_things_fragment.*
import java.util.*


class FiveThingsFragment : Fragment() {

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FiveThingsFragmentBinding
    private lateinit var yearList: MutableList<String>
    private lateinit var currentDate: Date

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user?.let {
            val accountManager = AccountManager.get(context)
            viewModel = FiveThingsViewModel(accountManager)

            binding = FiveThingsFragmentBinding.inflate(inflater, container, false)
            binding.viewModel = viewModel

            currentDate = Date()

            viewModel.getFiveThings(Date()).observe(this, Observer<Resource<FiveThingz>> { fiveThings ->
                binding.fiveThings = fiveThings?.data
                binding.naguDate = fiveThings?.data?.naguDate?.date
            })

            binding.calendarVisible = false


            return binding.root
        }
        //TODO handle case where user get here without logging in
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.five_things_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val compactCalendarView = view?.findViewById<CompactCalendarView>(R.id.compactcalendar_view)
        if (compactCalendarView != null) {

            viewModel.getDate().observe(this, Observer<Date> { date ->
                val currDate = date ?: Date()
                Log.d("blerg", "currdate: " + currDate)
                binding.naguDate = currDate
                binding.month = getMonth(currDate) + " " + getYear(currDate)
                compactCalendarView.setCurrentDate(currDate)
            })

            binding.loading = true

            viewModel.getWrittenDays().observe(this, Observer<Resource<List<Date>>> { days ->
                days?.let{
                    Log.d("blerg", "updating cal")
                    binding.loading = false
                    compactCalendarView.removeAllEvents()
                    days.data?.let {
                        val events = days.data.map { convertDateToEvent(it) }
                        compactCalendarView.addEvents(events)

                        yearList = mutableListOf()
                        val minYear = getYear(Collections.min(days.data))
                        val maxYear = getYear(Collections.max(days.data))
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
                                            compactCalendarView.setCurrentDate(newDate)
                                        })
                                        .create()
                                        .show()
                            }
                        }
                    }
                }
            })

            compactCalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
                override fun onDayClick(dateClicked: Date) {
                    val events = compactCalendarView.getEvents(dateClicked)
                    Log.d("blerg", "Day was clicked: $dateClicked with events $events")
                    viewModel.changeDate(dateClicked)
                    binding.calendarVisible = false
                }

                override fun onMonthScroll(firstDayOfNewMonth: Date) {
                    currentDate = firstDayOfNewMonth
                    binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
                }
            })
        }

        current_date.setOnClickListener {
            val currentVisibility = binding.calendarVisible
            currentVisibility?.let {
                binding.calendarVisible = !currentVisibility
            }
        }
    }

    //TODO handle when user tries to leave fragment with un-saved changes
}
