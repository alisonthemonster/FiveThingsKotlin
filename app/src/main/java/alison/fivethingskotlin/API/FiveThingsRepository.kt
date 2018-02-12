package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*

interface FiveThingsRepository {

    fun getFiveThings(date: Date, fiveThingsData: MutableLiveData<Resource<FiveThings>>): LiveData<Resource<FiveThings>>

    fun saveFiveThings(fiveThings: FiveThings, fiveThingsData: MutableLiveData<Resource<FiveThings>>)

    fun getWrittenDates(): MutableLiveData<Resource<List<Date>>>

}
