package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.convertDateToEvent
import alison.fivethingskotlin.Util.getMonth
import alison.fivethingskotlin.Util.getPreviousDate
import alison.fivethingskotlin.Util.getYear
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        user?.let {
            viewModel = FiveThingsViewModel(user)

            binding = FiveThingsFragmentBinding.inflate(inflater!!, container, false)
            binding.viewModel = viewModel
            viewModel.getFiveThings(Date()).observe(this, Observer<FiveThings> { fiveThings ->
                binding.fiveThings = fiveThings
            })

            binding.calendarVisible = false


            return binding.root
        }
        //TODO handle case where user get here without logging in
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.five_things_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val compactCalendarView = view?.findViewById<CompactCalendarView>(R.id.compactcalendar_view)
        if (compactCalendarView != null) {

            viewModel.getDate().observe(this, Observer<Date> { date ->
                if (date != null) {
                    binding.date = date
                    binding.month = getMonth(date) + " " + getYear(date)
                    compactCalendarView.setCurrentDate(date)
                }
            })

            binding.loading = true

            //TODO only pull in for current month?
            viewModel.getWrittenDays().observe(this, Observer<List<Date>> { days ->
                days?.let{
                    Log.d("blerg", "updating cal")
                    binding.loading = false
                    compactCalendarView.removeAllEvents()
                    val events = days.map { convertDateToEvent(it) }
                    compactCalendarView.addEvents(events)

                    yearList = mutableListOf()
                    val minYear = getYear(Collections.min(days))
                    val maxYear = getYear(Collections.max(days))
                    (minYear..maxYear).mapTo(yearList) { it.toString() }
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
                    binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
                }
            })
        }

        val calendarMonth = month_year
        calendarMonth.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)

            dialogBuilder.setTitle("Select a year")
                    .setItems(yearList.toTypedArray(), { _, year ->
                        Log.d("blerg", "year: " + yearList[year])
                        val newDate = GregorianCalendar(yearList[year].toInt(), Calendar.FEBRUARY, 1).time
                        Log.d("blerg", "newDate: " + newDate)
                        binding.month = getMonth(newDate) + " " + getYear(newDate)
                        compactCalendarView?.setCurrentDate(newDate)
                    })
                    .create()
            dialogBuilder.show()
        }

        val date = current_date
        date.setOnClickListener {
            val currentVisibility = binding.calendarVisible
            currentVisibility?.let {
                binding.calendarVisible = !currentVisibility
            }
        }
    }

    //TODO handle when user tries to leave fragment with un-saved changes
}
