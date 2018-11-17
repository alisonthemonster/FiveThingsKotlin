package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.databinding.FragmentFiveThingsBinding
import alison.fivethingskotlin.model.Thing
import alison.fivethingskotlin.util.getDatabaseStyleDate
import alison.fivethingskotlin.util.getDateFromFullDateFormat
import alison.fivethingskotlin.util.handleErrorState
import alison.fivethingskotlin.util.restoreAuthState
import alison.fivethingskotlin.viewmodel.FiveThingsViewModel
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import net.openid.appauth.AuthorizationService
import java.util.*
import java.util.concurrent.TimeUnit

class FiveThingsFragment : Fragment() {

    private lateinit var viewModel: FiveThingsViewModel
    private lateinit var binding: FragmentFiveThingsBinding
    private lateinit var currentDate: Date
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val compositeDisposable = CompositeDisposable()

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

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(FiveThingsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding.viewModel = viewModel
        viewModel.isLoading.set(true)

        val passedInDate = arguments?.getString(DATE)

        currentDate = if (passedInDate != null)
            getDateFromFullDateFormat(passedInDate) else Date()

        observeErrors()
        getFiveThings()
        setUpTextListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.setCurrentScreen(activity as Activity, "FiveThingsScreen", null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeErrors() {
        viewModel.errorLiveEvent.observe(this, Observer {
            Crashlytics.logException(Exception("Message: ${it?.capitalize()}"))
            handleErrorState(it ?: "Unknown Error", context!!)
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
                            viewModel.saveDay("Bearer $idToken", it.toTypedArray())
                        }
                compositeDisposable.add(disposable)
            }
        }

    }

    private fun getFiveThings() {
        val authorizationService = AuthorizationService(context!!)
        val authState = restoreAuthState(context!!)

        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState(ex.localizedMessage, context!!)
            } else {
                viewModel.getThings("Bearer $idToken", currentDate)
            }
        }
    }


}
