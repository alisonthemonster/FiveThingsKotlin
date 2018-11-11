package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.databinding.FragmentFiveThingsBinding
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.model.Thing
import alison.fivethingskotlin.util.*
import alison.fivethingskotlin.viewmodel.FiveThingsViewModel
import android.animation.ObjectAnimator
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.crashlytics.android.Crashlytics
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.firebase.analytics.FirebaseAnalytics
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.fragment_five_things.*
import net.openid.appauth.AuthorizationService
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


class FiveThingsFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FragmentFiveThingsBinding
    private lateinit var yearList: MutableList<String>
    private lateinit var currentDate: Date
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var inCloud: Boolean = false
    private val compositeDisposable = CompositeDisposable()

    companion object {

        const val DATE = "date_key"
        const val HAS_OPENED_CALENDAR = "has_opened_calendar"

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
        binding.saving = false
        binding.inEditMode = false

        viewModel = ViewModelProviders.of(this).get(FiveThingsViewModel::class.java)

        binding.viewModel = viewModel
        binding.calendarVisible = false

        val passedInDate = arguments?.getString(DATE)

        currentDate = if (passedInDate != null)
            getDateFromFullDateFormat(passedInDate) else Date()

        startObserving()
        getFiveThings()
        getWrittenDays()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        setUpTextListeners()
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics.setCurrentScreen(activity as Activity, "FiveThingsScreen", null)
    }

    override fun onStop() {
        super.onStop()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun animateDate() {
        val animator = ObjectAnimator.ofFloat(current_date, "translationY", 0f, 20f, 0f)
        animator.interpolator = BounceInterpolator()
        animator.startDelay = 2000
        animator.duration = 1500
        animator.start()
    }

    private fun startObserving() {
        viewModel.datesLiveData().observe(this, Observer<Resource<List<Date>>> { dates ->
            when (dates?.status) {
                Status.SUCCESS -> {
                    addEventsToCalendar(dates.data)
                    if (dates.message == "A day was changed") {
                        binding.saving = false
                        inCloud = true
                    }
                }
                Status.ERROR -> {
                    binding.loading = false
                    val message = dates.message!!.capitalize()
                    Crashlytics.logException(Exception("Saving error, date: ${binding.fiveThings?.date}  Message: $message"))
                    handleErrorState(message, context!!)
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
                    binding.loading = false
                    inCloud = !things.data.isEmpty //if there's data there it came from the server
                }
                Status.ERROR -> {
                    binding.loading = false
                    val message = things.message!!.capitalize()
                    Crashlytics.logException(Exception("Saving error, date: ${binding.fiveThings?.date}  Message: $message"))
                    handleErrorState(message, context!!)
                }
            }
        })
    }

    private fun setUpTextListeners() {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }
        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                binding.loading = false
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                val one = RxTextView.afterTextChangeEvents(binding.one)
                val two = RxTextView.afterTextChangeEvents(binding.two)
                val three = RxTextView.afterTextChangeEvents(binding.three)
                val four = RxTextView.afterTextChangeEvents(binding.four)
                val five = RxTextView.afterTextChangeEvents(binding.five)

                val disposable = Observables.combineLatest(one, two, three, four, five) { oneEvent, twoEvent, threeEvent, fourEvent, fiveEvent ->
                    listOf(Thing(getDatabaseStyleDate(currentDate), oneEvent.editable().toString(), 1),
                            Thing(getDatabaseStyleDate(currentDate), twoEvent.editable().toString(), 2),
                            Thing(getDatabaseStyleDate(currentDate), threeEvent.editable().toString(), 3),
                            Thing(getDatabaseStyleDate(currentDate), fourEvent.editable().toString(), 4),
                            Thing(getDatabaseStyleDate(currentDate), fiveEvent.editable().toString(), 5))
                }
                        .skip(1) //skip the edit text binding
                        .debounce(1000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            if (inCloud) {
                                binding.saving = true
                                viewModel.updateThings("Bearer $idToken", it.toTypedArray())
                            } else {
                                binding.saving = true
                                viewModel.saveNewThings("Bearer $idToken", it.toTypedArray())
                            }
                        }
                compositeDisposable.add(disposable)
            }
        }

        setEditTextClickListeners()
    }

    private fun setEditTextClickListeners() {
        binding.one.setOnLongClickListener { toggleEditMode(binding.one) }
        binding.one.setOnClickListener { editTextClick() }
        binding.two.setOnLongClickListener { toggleEditMode(binding.two) }
        binding.two.setOnClickListener { editTextClick() }
        binding.three.setOnLongClickListener { toggleEditMode(binding.three) }
        binding.three.setOnClickListener { editTextClick() }
        binding.four.setOnLongClickListener { toggleEditMode(binding.four) }
        binding.four.setOnClickListener { editTextClick() }
        binding.five.setOnLongClickListener { toggleEditMode(binding.five) }
        binding.five.setOnClickListener { editTextClick() }
    }

    private fun toggleEditMode(editText: EditText): Boolean {
        binding.inEditMode = !(binding.inEditMode)!!

        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (binding.inEditMode == false) {
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        } else {
            editText.requestFocus()
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        return true
    }

    private fun editTextClick() {
        if (!binding.inEditMode!!)
            makeToast(context!!, "Long press to edit a day")
    }

    private fun getFiveThings() {
        binding.loading = true
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

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
        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                binding.loading = false
                Crashlytics.logException(Exception("GET days error, Message: ${ex.localizedMessage}"))
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
