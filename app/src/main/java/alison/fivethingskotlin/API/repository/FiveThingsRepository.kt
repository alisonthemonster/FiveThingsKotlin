package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*

interface FiveThingsRepository {

    fun getFiveThings(token: String, date: Date, fiveThingsData: MutableLiveData<Resource<FiveThings>>): LiveData<Resource<FiveThings>>

    fun saveFiveThings(token: String, fiveThings: FiveThings, fiveThingsData: MutableLiveData<Resource<FiveThings>>): MutableLiveData<Resource<List<Date>>>

    fun getWrittenDates(token: String): MutableLiveData<Resource<List<Date>>>

}
