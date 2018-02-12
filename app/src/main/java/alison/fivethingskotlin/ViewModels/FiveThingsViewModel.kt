package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.API.FiveThingsFirebaseRepository
import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.getNextDate
import alison.fivethingskotlin.Util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import java.util.*


class FiveThingsViewModel(private val user: FirebaseUser): ViewModel() {

    private val fiveThingsData = MutableLiveData<Resource<FiveThings>>()
    private val dateData = MutableLiveData<Date>()
    private val firebaseSource = FiveThingsFirebaseRepository(user)

    fun getFiveThings(date: Date): LiveData<Resource<FiveThings>> {
        dateData.value = date
        return firebaseSource.getFiveThings(date, fiveThingsData)
    }

    fun getDate(): LiveData<Date> {
        return dateData
    }

    fun onEditText() {
        val fiveThings = fiveThingsData.value
        fiveThings?.data?.saved = false
        fiveThingsData.value = fiveThings
    }

    fun writeFiveThings(fiveThings: FiveThings) {
        Log.d("fivethings", "about to write the data: " + fiveThings)
        firebaseSource.saveFiveThings(fiveThings, fiveThingsData)
    }

    fun getToday(): LiveData<Resource<FiveThings>> {
        return getFiveThings(Date())
    }

    fun getPreviousDay(date: Date): LiveData<Resource<FiveThings>> {
        val prevDate = getPreviousDate(date)
        return getFiveThings(prevDate)
    }

    fun getNextDay(date: Date): LiveData<Resource<FiveThings>>  {
        val nextDate = getNextDate(date)
        return getFiveThings(nextDate)
    }

    fun changeDate(date: Date): LiveData<Resource<FiveThings>> {
        return getFiveThings(date)
    }

    fun getWrittenDays(): LiveData<Resource<List<Date>>> {
        return firebaseSource.getWrittenDates()
    }

}