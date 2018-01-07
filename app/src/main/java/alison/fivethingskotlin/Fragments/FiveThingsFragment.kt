package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.Observer
import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.*
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import com.github.sundeepk.compactcalendarview.CompactCalendarView

class FiveThingsFragment : Fragment() {

    val user = FirebaseAuth.getInstance().currentUser
    var eventsLoaded = false
    var date = Date()
    lateinit var viewModel: FiveThingsViewModel
    lateinit var binding: FiveThingsFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

            binding.month = getMonth(date) + " " + getYear(date)

            return binding.root
        }
        //TODO handle case where user get here without logging in
        return inflater!!.inflate(R.layout.five_things_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val compactCalendarView = view?.findViewById<CompactCalendarView>(R.id.compactcalendar_view)
        if (compactCalendarView != null) {

            binding.loading = true

            //TODO only pull in for current month?
            viewModel.getWrittenDays().observe(this, Observer<List<Date>> { days ->
                days?.let{
                    binding.loading = false
                    if (!eventsLoaded) {
                        val events = days.map { convertDateToEvent(it) }
                        compactCalendarView.addEvents(events)
                        eventsLoaded = true
                    }
                }
            })

            compactCalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
                override fun onDayClick(dateClicked: Date) {
                    date = dateClicked
                    viewModel.changeDate(dateClicked)
                    binding.calendarVisible = false
                }

                override fun onMonthScroll(firstDayOfNewMonth: Date) {
                    binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
                }
            })
        }

        val dateView = view?.findViewById<TextView>(R.id.current_date)
        dateView?.setOnClickListener {
            val currentVisibility = binding.calendarVisible
            currentVisibility?.let {
                binding.calendarVisible = !currentVisibility
            }
        }

        val things = view?.findViewById<LinearLayout>(R.id.things)
        things?.setOnTouchListener(object : OnSwipeTouchListener(context){
            override fun onSwipeRight() {
                Log.d("swipe", "swiped right!")
                date = getPreviousDate(date)
                compactCalendarView?.setCurrentDate(date)
                viewModel.changeDate(date)
            }
            override fun onSwipeLeft() {
                Log.d("swipe", "swiped left!")
                date = getNextDate(date)
                compactCalendarView?.setCurrentDate(date)
                viewModel.changeDate(date)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        eventsLoaded = false
    }

    //TODO handle when user tries to leave fragment with un-saved changes
}
