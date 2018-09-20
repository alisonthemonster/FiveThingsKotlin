package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.databinding.FragmentFiveThingsBinding
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.util.*
import alison.fivethingskotlin.viewmodel.FiveThingsViewModel
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_five_things.*
import net.openid.appauth.AuthorizationService
import org.reactivestreams.Subscription
import java.util.*
import java.util.concurrent.TimeUnit


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

        viewModel = ViewModelProviders.of(this).get(FiveThingsViewModel::class.java)

        binding.viewModel = viewModel
        binding.calendarVisible = false

        val passedInDate = arguments?.getString(DATE)

        currentDate = if (passedInDate != null)
            getDateFromFullDateFormat(passedInDate) else Date()

        startObserving()
        //setUpTextListeners()
        getFiveThings()
        getWrittenDays()

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
    }

    private fun startObserving() {
        viewModel.datesLiveData().observe(this, Observer<Resource<List<Date>>> { dates ->
            when (dates?.status) {
                Status.SUCCESS -> {
                    Log.d("blerg", "we got something yo")
                    addEventsToCalendar(dates.data)
                }
                Status.ERROR -> {
                    binding.loading = false
                    handleErrorState(dates.message!!.capitalize(), context!!)
                }
            }
        })

        viewModel.thingsLiveData().observe(this, Observer<Resource<FiveThings>> { things ->
            when (things?.status) {
                Status.SUCCESS -> {
                    binding.fiveThings = things.data
                    val date = things.data?.date!!
                    binding.naguDate = date
                    binding.month = getMonth(date) + " " + getYear(date)
                    compactcalendar_view.setCurrentDate(date)
                    binding.loading = false
                }
                Status.ERROR -> {
                    binding.loading = false
                    handleErrorState(things.message!!.capitalize(), context!!)
                }
            }
        })
    }

    private var subscription: Subscription? = null

    private fun setUpTextListeners() {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                binding.loading = false
                handleErrorState(ex.localizedMessage, context!!)
            } else {

                //when ever the text in the 1st text box changes send an update to server
                //TODO how to know when to update vs to add an entry
                RxTextView.afterTextChangeEvents(one)
                        .debounce(1000, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { tvChangeEvent ->
                            val text = tvChangeEvent.view().text.toString()
                            viewModel.updateThing("Bearer $idToken", text, 1, currentDate)
                        }
            }
        }

    }

    private fun getFiveThings() {
        binding.loading = true
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        //TODO if authstate is null show error

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                binding.loading = false
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getThings("Bearer $idToken", currentDate)
            }
        }

    }

    private fun getWrittenDays() {
        binding.loading = true
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                binding.loading = false
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getDays("Bearer $idToken")
            }
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
