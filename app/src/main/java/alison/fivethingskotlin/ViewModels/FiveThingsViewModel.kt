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

    private var database = FirebaseDatabase.getInstance().reference
    private val fiveThingsData = MutableLiveData<FiveThings>()
    private val fiveThingsDates = MutableLiveData<List<Date>>()
    private val firebaseSource = FirebaseSource(user)

    fun getFiveThings(date: Date): LiveData<FiveThings> {
        return firebaseSource.getFiveThings(date)
    }

    fun onEditText() {
        val fiveThings = fiveThingsData.value
        fiveThings?.saved = false
        fiveThingsData.value = fiveThings
    }

    fun writeFiveThings(fiveThings: FiveThings) {
        Log.d("fivethings", "about to write the data: " + fiveThings)
        val things = ArrayList<String>()
        things.add(fiveThings.one)
        things.add(fiveThings.two)
        things.add(fiveThings.three)
        things.add(fiveThings.four)
        things.add(fiveThings.five)

        val formattedDate = getDatabaseStyleDate(fiveThings.date)

        val child = database.child("users").child(user.uid).child(formattedDate)
        if (fiveThings.isEmpty) {
            //user hasn't written or has deleted a whole day
            child.setValue(null)
        } else {
            child.setValue(things) { error, ref ->
                if (error != null) {
                    fiveThings.saved = true
                    fiveThingsData.value = fiveThings
                    Log.d("fivethings", "No error: " + ref)
                }
            }
        }
    }


//    fun writeFiveThings(fiveThings: FiveThings) {
//        Log.d("fivethings", "about to write the data: " + fiveThings)
//
//
//        firebaseSource.saveFiveThings(fiveThings, fiveThingsData)
//
//
//    }

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