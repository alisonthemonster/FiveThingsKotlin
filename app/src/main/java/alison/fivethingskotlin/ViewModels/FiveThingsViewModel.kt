package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.Models.FirebaseSource
import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.getNextDate
import alison.fivethingskotlin.Util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import java.util.*


class FiveThingsViewModel(user: FirebaseUser): ViewModel() {

    private val fiveThingsData = MutableLiveData<FiveThings>()
    private val dateData = MutableLiveData<Date>()
    private val firebaseSource = FirebaseSource(user)

    fun getFiveThings(date: Date): LiveData<FiveThings> {
        dateData.value = date
        return firebaseSource.getFiveThings(date, fiveThingsData)
    }

    fun getDate(): LiveData<Date> {
        return dateData
    }

    fun onEditText() {
        val fiveThings = fiveThingsData.value
        fiveThings?.saved = false
        fiveThingsData.value = fiveThings
    }

    fun writeFiveThings(fiveThings: FiveThings) {
        Log.d("fivethings", "about to write the data: " + fiveThings)
        firebaseSource.saveFiveThings(fiveThings, fiveThingsData)
    }

    fun getToday(): LiveData<FiveThings> {
        return getFiveThings(Date())
    }

    fun getPreviousDay(date: Date): LiveData<FiveThings> {
        val prevDate = getPreviousDate(date)
        return getFiveThings(prevDate)
    }

    fun getNextDay(date: Date): LiveData<FiveThings>  {
        val nextDate = getNextDate(date)
        return getFiveThings(nextDate)
    }

    fun changeDate(date: Date): LiveData<FiveThings> {
        return getFiveThings(date)
    }

    fun getWrittenDays(): LiveData<List<Date>> {
        return firebaseSource.getWrittenDates()
    }

}