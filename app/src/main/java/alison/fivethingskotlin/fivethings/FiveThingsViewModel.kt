package alison.fivethingskotlin.fivethings

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.model.Thing
import alison.fivethingskotlin.util.*
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*


class FiveThingsViewModel(private val fiveThingsService: FiveThingsService = FiveThingsService.create()) : ViewModel() {

    private val datesLiveData = MutableLiveData<Resource<List<Date>>>()

    val calendarOpenEvent = SingleLiveEvent<Void>()
    val closeCalendarEvent = SingleLiveEvent<Void>()
    val errorLiveEvent = SingleLiveEvent<String>()

    val month = ObservableField<String>()
    val dateString = ObservableField<String>()

    val one = ObservableField<String>()
    val two = ObservableField<String>()
    val three = ObservableField<String>()
    val four = ObservableField<String>()
    val five = ObservableField<String>()

    val saved = ObservableField<Boolean>()
    val isSaving = ObservableField<Boolean>()
    val isLoading = ObservableField<Boolean>()

    private val disposables = CompositeDisposable()

    init {
        month.set(getMonth(Date()) + " " + getYear(Date()))
    }

    fun saveDay(token: String, things: Array<Thing>) {
        val savedValue = saved.get() ?: false
        if (savedValue) {
            saved.set(false)
            isSaving.set(true)
            updateThings(token, things)
        } else {
            saved.set(false)
            isSaving.set(true)
            saveNewThings(token, things)
        }
    }

    private fun updateThings(token: String, things: Array<Thing>) {
        disposables.add(fiveThingsService.updateFiveThings(token, things)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { writtenDates ->
                            val days = writtenDates.map { getDateFromDatabaseStyle(it) }
                            datesLiveData.postValue(Resource(Status.SUCCESS, "A day was changed", days))
                            saved.set(true)
                            isSaving.set(false)
                        },
                        { error ->
                            saved.set(false)
                            isSaving.set(false)
                            datesLiveData.postValue(Resource(Status.ERROR, error.message, emptyList()))
                        }
                ))
    }

    private fun saveNewThings(token: String, things: Array<Thing>) {
        disposables.add(fiveThingsService.writeFiveThings(token, things)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { writtenDates ->
                            val days = writtenDates.map { getDateFromDatabaseStyle(it) }
                            datesLiveData.postValue(Resource(Status.SUCCESS, "A day was changed", days))
                            saved.set(true)
                            isSaving.set(false)
                        },
                        { error ->
                            saved.set(false)
                            isSaving.set(false)
                            datesLiveData.postValue(Resource(Status.ERROR, error.message, emptyList()))
                        }
                ))
    }

    fun getThings(token: String, date: Date) {
        isLoading.set(true)
        disposables.add(fiveThingsService.getFiveThings(token, getYear(date).toString(),
                String.format("%02d", getMonthNumber(date)),
                String.format("%02d", getDay(date)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { things ->
                            for (thing in things) {
                                when (thing.order) {
                                    1 -> one.set(thing.content)
                                    2 -> two.set(thing.content)
                                    3 -> three.set(thing.content)
                                    4 -> four.set(thing.content)
                                    5 -> five.set(thing.content)
                                }
                            }
                            saved.set(true)
                            dateString.set(getFullDateFormat(date))
                            isLoading.set(false)
                        },
                        { error ->
                            if (error is retrofit2.HttpException && error.code() == 404) {
                                saved.set(false)
                                dateString.set(getFullDateFormat(date))
                                one.set("")
                                two.set("")
                                three.set("")
                                four.set("")
                                five.set("")
                                isLoading.set(false)
                            } else {
                                errorLiveEvent.postValue(error.localizedMessage)
                                saved.set(false)
                                isLoading.set(false)
                            }
                        }
                ))
    }

    fun getDays(token: String): MutableLiveData<Resource<List<Date>>> {
        disposables.add(fiveThingsService.getWrittenDates(token)
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
        return datesLiveData
    }

    fun openCalendar() {
        calendarOpenEvent.call()
    }

    fun closeCalendar() {
        closeCalendarEvent.call()
    }

    override fun onCleared() {
        disposables.clear()
    }

}