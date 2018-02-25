package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.FiveThingz
import alison.fivethingskotlin.Util.Resource
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*

interface FiveThingsRepository {

    fun getFiveThings(token: String, date: Date, fiveThingsData: MutableLiveData<Resource<FiveThingz>>): LiveData<Resource<FiveThingz>>

    fun saveFiveThings(token: String, fiveThings: FiveThingz, fiveThingsData: MutableLiveData<Resource<FiveThingz>>)

    fun getWrittenDates(token: String): MutableLiveData<Resource<List<Date>>>

}
