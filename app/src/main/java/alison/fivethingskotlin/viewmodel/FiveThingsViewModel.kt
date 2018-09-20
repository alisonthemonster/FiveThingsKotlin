package alison.fivethingskotlin.viewmodel

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.model.Thing
import alison.fivethingskotlin.util.*
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.HttpException
import java.util.*


class FiveThingsViewModel(private val fiveThingsService: FiveThingsService = FiveThingsService.create()) : ViewModel() {

    private val fiveThingsData = MutableLiveData<Resource<FiveThings>>()
    private val datesLiveData = MutableLiveData<Resource<List<Date>>>()

    private val disposables = CompositeDisposable()

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
                            if (error is retrofit2.HttpException && error.code() == 404) {
                                val things = FiveThings(date, listOf(
                                        Thing(getDatabaseStyleDate(date), "", 1),
                                        Thing(getDatabaseStyleDate(date), "", 2),
                                        Thing(getDatabaseStyleDate(date), "", 3),
                                        Thing(getDatabaseStyleDate(date), "", 4),
                                        Thing(getDatabaseStyleDate(date), "", 5)),
                                        false,
                                        false)
                                fiveThingsData.value = Resource(Status.SUCCESS, "Unwritten Day", things)
                            } else {
                                datesLiveData.postValue(Resource(Status.ERROR, error.message, null))
                            }
                        }
                ))
    }

    fun getDays(token: String) {
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

    fun datesLiveData(): MutableLiveData<Resource<List<Date>>> {
        return datesLiveData
    }

    fun thingsLiveData(): MutableLiveData<Resource<FiveThings>> {
        return fiveThingsData
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