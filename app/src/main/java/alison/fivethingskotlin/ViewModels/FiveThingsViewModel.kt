package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.Models.FirebaseSource
import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.getDatabaseStyleDate
import alison.fivethingskotlin.Util.getDateFromDatabaseStyle
import alison.fivethingskotlin.Util.getNextDate
import alison.fivethingskotlin.Util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


class FiveThingsViewModel(private val user: FirebaseUser): ViewModel() {

    private val fiveThingsData = MutableLiveData<FiveThings>()
    private val firebaseSource = FirebaseSource(user)

    fun getFiveThings(date: Date): LiveData<FiveThings> {
        return firebaseSource.getFiveThings(date, fiveThingsData)
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