package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.FiveThingz
import android.arch.lifecycle.Observer
import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.convertDateToEvent
import alison.fivethingskotlin.Util.getMonth
import alison.fivethingskotlin.Util.getYear
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
import android.accounts.AccountManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import com.github.sundeepk.compactcalendarview.CompactCalendarView


class FiveThingsFragment : Fragment() {

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FiveThingsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        user?.let {
            val accountManager = AccountManager.get(context)
            viewModel = FiveThingsViewModel(accountManager)

            binding = FiveThingsFragmentBinding.inflate(inflater, container, false)
            binding.viewModel = viewModel
            viewModel.getFiveThings(Date()).observe(this, Observer<Resource<FiveThingz>> { fiveThings ->
                binding.fiveThings = fiveThings?.data
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
                binding.date = currDate
                binding.month = getMonth(currDate) + " " + getYear(currDate)
                compactCalendarView.setCurrentDate(currDate)
            })

            binding.loading = true

            //TODO only pull in for current month?
            viewModel.getWrittenDays().observe(this, Observer< Resource<List<Date>>> { days ->
                days?.let{
                    Log.d("blerg", "updating cal")
                    binding.loading = false
                    compactCalendarView.removeAllEvents()
                    days.data?.let {
                        val events = days.data?.map { convertDateToEvent(it) }

                        compactCalendarView.addEvents(events)
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
                    binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
                }
            })
        }

        val date = view?.findViewById<TextView>(R.id.current_date)
        date?.setOnClickListener {
            val currentVisibility = binding.calendarVisible
            currentVisibility?.let {
                binding.calendarVisible = !currentVisibility
            }
        }
    }

    //TODO handle when user tries to leave fragment with un-saved changes
}
