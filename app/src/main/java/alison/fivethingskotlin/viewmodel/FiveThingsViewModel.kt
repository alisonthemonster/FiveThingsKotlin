package alison.fivethingskotlin.viewmodel

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.api.repository.FiveThingsRepository
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.model.Thing
import alison.fivethingskotlin.util.*
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import java.util.*


class FiveThingsViewModel(private val fiveThingsRepository: FiveThingsRepository, private val authState: AuthState?, private val authorizationService: AuthorizationService) : ViewModel() {

    private val fiveThingsData = MutableLiveData<Resource<FiveThings>>()
    private val datesLiveData = MutableLiveData<Resource<List<Date>>>()

    private val fiveThingsService: FiveThingsService = FiveThingsService.create()


    fun getFiveThings(date: Date): LiveData<Resource<FiveThings>> {

        if (authState == null) {
            fiveThingsData.postValue(Resource(Status.ERROR, "Log in failed", null))
        }
        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                fiveThingsData.postValue(Resource(Status.ERROR, "Log in failed: ${ex.errorDescription}", null))
            } else {
                idToken?.let {
                    val things = fiveThingsRepository.getFiveThings("Bearer $idToken", date, fiveThingsData)
                    fiveThingsData.postValue(things.value)
                }
            }
        }
        return fiveThingsData
    }

    private val disposables = CompositeDisposable()

    fun datesLiveData(): MutableLiveData<Resource<List<Date>>> {
        return datesLiveData
    }

    fun thingsLiveData(): MutableLiveData<Resource<FiveThings>> {
        return fiveThingsData
    }

    fun updateThing(token: String, content: String, order: Int, date: Date) {


        disposables.add(fiveThingsService.updateFiveThingsRx(token, arrayOf(Thing(getDatabaseStyleDate(date), content, order)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { writtenDates ->
                            val days = writtenDates.map { getDateFromDatabaseStyle(it) }
                            datesLiveData.postValue(Resource(Status.SUCCESS, "", days))
                        },
                        { error ->
                            datesLiveData.postValue(Resource(Status.ERROR, error.message, emptyList()))
                        }
                ))
    }

    fun saveNewThing(token: String, content: String, order: Int, date: Date) {

        //TODO doOnSubscribe to handle loading
        disposables.add(fiveThingsService.writeFiveThingsRx(token, arrayOf(Thing(getDatabaseStyleDate(date), content, order)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { writtenDates ->
                            val days = writtenDates.map { getDateFromDatabaseStyle(it) }
                            datesLiveData.postValue(Resource(Status.SUCCESS, "", days))
                        },
                        { error ->
                            datesLiveData.postValue(Resource(Status.ERROR, error.message, emptyList()))
                        }
                ))
    }

    fun getThings(token: String, date: Date) {
        disposables.add(fiveThingsService.getFiveThingsRx(token,  getYear(date).toString(),
                                                String.format("%02d", getMonthNumber(date)),
                                                String.format("%02d", getDay(date)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { things ->
                            val fiveThings = FiveThings(date, things, false, true)
                            fiveThingsData.postValue(Resource(Status.SUCCESS, "", fiveThings))
                        },
                        { error ->
                            datesLiveData.postValue(Resource(Status.ERROR, error.message, null))
                        }
                ))
    }

    fun getDays(token: String): LiveData<Resource<List<Date>>> {
        disposables.add(fiveThingsService.getWrittenDatesRx(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { dates ->
                            val days = dates.map { getDateFromDatabaseStyle(it) }
                            datesLiveData.postValue(Resource(Status.SUCCESS, "", days))
                        },
                        { error ->
                            datesLiveData.postValue(Resource(Status.ERROR, error.message, null))
                        }
                ))
    }

    override fun onCleared() {
        disposables.clear()
    }

    private var editCount = -1

    fun onEditText() {
        //HACKY FIX: ignores the first edit texts that occur thanks to data binding
        val fiveThings = fiveThingsData.value
        when {
            editCount == -1 -> {
                editCount = fiveThings?.data?.thingsCount!! - 1
            }
            editCount <= 0 -> {
                fiveThings?.data?.edited = true
                fiveThingsData.value = fiveThings
            }
            else -> editCount--
        }
    }

}