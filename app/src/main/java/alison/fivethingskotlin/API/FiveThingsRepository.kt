package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*

interface FiveThingsRepository {

    fun getFiveThings(date: Date, fiveThingsData: MutableLiveData<FiveThings>): LiveData<FiveThings>

    fun saveFiveThings(fiveThings: FiveThings, fiveThingsData: MutableLiveData<FiveThings>)

    fun getWrittenDates(): MutableLiveData<List<Date>>

}
