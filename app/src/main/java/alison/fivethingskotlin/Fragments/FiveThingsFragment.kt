package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.API.repository.FiveThingsRepositoryImpl
import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.PromoActivity
import alison.fivethingskotlin.Util.*
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.ViewModels.FiveThingsViewModelFactory
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.five_things_fragment.*
import net.openid.appauth.AuthorizationService
import java.util.*


class FiveThingsFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FiveThingsFragmentBinding
    private lateinit var yearList: MutableList<String>
    private lateinit var currentDate: Date

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FiveThingsFragmentBinding.inflate(inflater, container, false)
        binding.loading = true

        context?.let {
            val authorizationService = AuthorizationService(it)
            val authState = restoreAuthState(it)

            //TODO move the fresh tokens into the view models?
                //fresh token should be fetched for every call?
            authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
                if (ex != null) {
                    Log.e("blerg", "Negotiation for fresh tokens failed: $ex")
                    binding.loading = false
                    showErrorDialog(ex.localizedMessage, context!!, "Log in again", openLogInScreen())
                    //TODO show error here
                } else {
                    idToken?.let {
                        viewModel = ViewModelProviders.of(this,
                                            FiveThingsViewModelFactory("Bearer $it",
                                                                        FiveThingsRepositoryImpl()))
                                .get(FiveThingsViewModel::class.java)

                        binding.viewModel = viewModel

                        val passedInDate = arguments?.getString("dateeee") //TODO move to constant

                        currentDate = if (passedInDate != null)
                            getDateFromDatabaseStyle(passedInDate) else Date()

                        getFiveThings()

                        getWrittenDays()
                    }
                }
            }
        }
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

    private fun openLogInScreen(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            val intent = Intent(context, PromoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun getFiveThings() {

        binding.loading = true

        viewModel.getFiveThings(currentDate).observe(this, Observer<Resource<FiveThings>> { fiveThings ->
            when (fiveThings?.status) {
                Status.SUCCESS -> {
                    Log.d("blerg", "bloop")
                    binding.fiveThings = fiveThings.data
                    fiveThings.data?.let {
                        binding.naguDate = it.date
                        binding.month = getMonth(it.date) + " " + getYear(it.date)
                        compactcalendar_view.setCurrentDate(it.date)
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
            binding.loading = false
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
