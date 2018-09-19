package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.api.repository.FiveThingsRepositoryImpl
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.util.*
import alison.fivethingskotlin.viewmodel.FiveThingsViewModel
import alison.fivethingskotlin.viewmodel.FiveThingsViewModelFactory
import alison.fivethingskotlin.databinding.FragmentFiveThingsBinding
import alison.fivethingskotlin.model.Resource
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.fragment_five_things.*
import net.openid.appauth.AuthorizationService
import java.util.*
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher


class FiveThingsFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FragmentFiveThingsBinding
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

        binding = FragmentFiveThingsBinding.inflate(inflater, container, false)
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
                binding.loading = true
                val activity = context as ContainerActivity
                activity.selectDate(dateClicked, false)
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

        todayButton.setOnClickListener {
            binding.loading = true
            val activity = context as ContainerActivity
            activity.selectDate(Date(), false)
        }

        setUpAutoSave()
    }

    private fun saveFiveThings() {
        viewModel.saveFiveThings(binding.fiveThings!!).observe(this, Observer<Resource<List<Date>>> {
            when (it?.status) {
                Status.SUCCESS -> addEventsToCalendar(it.data)
                Status.ERROR -> handleErrorState(it.message!!.capitalize(), context!!)
            }
        })
    }

    private fun setUpAutoSave() {
        val delay: Long = 1000 // 1 seconds after user stops typing
        var lastEditText: Long = 0
        val handler = Handler()

        val inputFinished = Runnable {
            if (System.currentTimeMillis() > lastEditText + delay - 500) {
                if (binding.fiveThings!!.edited)
                    saveFiveThings()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                       count: Int) {
                //You need to remove this to run only once
                handler.removeCallbacks(inputFinished)

            }

            override fun afterTextChanged(s: Editable) {
                lastEditText = System.currentTimeMillis()
                handler.postDelayed(inputFinished, delay)
            }
        }

        one.addTextChangedListener(textWatcher)
        two.addTextChangedListener(textWatcher)
        three.addTextChangedListener(textWatcher)
        four.addTextChangedListener(textWatcher)
        five.addTextChangedListener(textWatcher)
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
                    handleErrorState(fiveThings.message!!.capitalize(), context!!)
                    //Toast.makeText(context, fiveThings.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getWrittenDays() {
        //build calendar when days come back from server
        viewModel.getWrittenDays().observe(this, Observer<Resource<List<Date>>> { days ->
            days?.let {
                when (it.status) {
                    Status.SUCCESS -> addEventsToCalendar(it.data)
                    Status.ERROR -> handleErrorState(it.message!!.capitalize(), context!!)
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
                val dialogBuilder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
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

}
