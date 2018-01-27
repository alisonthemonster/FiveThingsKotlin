package alison.fivethingskotlin.Models

import alison.fivethingskotlin.Util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import java.util.*

class MockFirebaseSource(private val user: FirebaseUser) {

    //TODO find a way to wire this up for testing

    fun getFiveThings(date: Date, fiveThingsData: MutableLiveData<FiveThings>): LiveData<FiveThings> {
        val fiveThings = FiveThings(date,
                "The entry for the first thing",
                "The entry for the second thing",
                "The entry for the third thing",
                "The entry for the fourth thing",
                "The entry for the fifth thing",
                true)
        fiveThingsData.value = fiveThings
        Log.d("fivethings", "mock data set!")

        return fiveThingsData
    }

    fun saveFiveThings(fiveThings: FiveThings, fiveThingsData: MutableLiveData<FiveThings>) {
        fiveThings.saved = true
        fiveThingsData.value = fiveThings
    }

    fun getWrittenDates(): MutableLiveData<List<Date>> {
        val fiveThingsDates = MutableLiveData<List<Date>>()

        val yesterday = getPreviousDate(Date())
        val dayBeforeYesterday = getPreviousDate(yesterday)
        val dayBeforeDayBefore = getPreviousDate(dayBeforeYesterday)

        fiveThingsDates.value = listOf(yesterday, dayBeforeYesterday, dayBeforeDayBefore)
        return fiveThingsDates
    }
}
