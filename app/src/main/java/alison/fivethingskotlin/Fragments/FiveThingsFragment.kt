package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.Observer
import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.convertDaysToEvents
import alison.fivethingskotlin.Util.getMonth
import alison.fivethingskotlin.Util.getYear
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
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

    val user = FirebaseAuth.getInstance().currentUser
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

            viewModel.getWrittenDays().observe(this, Observer<List<Date>> { days ->
                days?.let{
                    val events = convertDaysToEvents(days)
                    compactCalendarView.addEvents(events)
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
                    Log.d("blerg", "Month was scrolled to: " + firstDayOfNewMonth)
                    binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
                    //todo fetch new months events?
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
}
