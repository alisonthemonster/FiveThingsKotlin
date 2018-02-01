package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*

class NagkumarSource() {

    fun getFiveThings(date: Date, fiveThingsData: MutableLiveData<FiveThings>): LiveData<FiveThings> {
        //TODO
        return fiveThingsData
    }

    fun saveFiveThings(fiveThings: FiveThings, fiveThingsData: MutableLiveData<FiveThings>) {
        //TODO
    }

    fun getWrittenDates(): MutableLiveData<List<Date>> {
        val fiveThingsDates = MutableLiveData<List<Date>>()
        //TODO
        return fiveThingsDates
    }
}
