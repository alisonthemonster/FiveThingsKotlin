package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.FiveThingsRequest
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.Models.Token
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.getDatabaseStyleDate
import alison.fivethingskotlin.Util.getDateFromDatabaseStyle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*



class FiveThingsRepositoryImpl(private val fiveThingsService: FiveThingsService = FiveThingsService.create()): FiveThingsRepository {

    override fun getFiveThings(date: Date, fiveThingsData: MutableLiveData<Resource<FiveThings>>): LiveData<Resource<FiveThings>> {
        val dateString = getDatabaseStyleDate(date)
        val call = fiveThingsService.getFiveThings(dateString)
        call.enqueue(object : Callback<FiveThings> {
            override fun onResponse(call: Call<FiveThings>?, response: Response<FiveThings>) {
                if (response.isSuccessful) {
                    fiveThingsData.value = Resource(Status.SUCCESS, "", response.body())
                } else {
                    fiveThingsData.value = Resource(Status.ERROR, response.message(), response.body())
                }
            }

            override fun onFailure(call: Call<FiveThings>?, t: Throwable?) {
                //TODO
                Log.d("blerg", "on failure babyyyy")
                t?.printStackTrace()
            }
        })
        return fiveThingsData
    }

    override fun saveFiveThings(fiveThings: FiveThings, fiveThingsData: MutableLiveData<Resource<FiveThings>>) {
        Log.d("blerg", "about to make request to save day")

        val requestBody = FiveThingsRequest(getDatabaseStyleDate(fiveThings.date),
                arrayOf(fiveThings.one, fiveThings.two, fiveThings.three, fiveThings.four, fiveThings.five))

        val call = fiveThingsService.writeFiveThings(requestBody)
        call.enqueue(object : Callback<> {
            override fun onResponse(call: Call<FiveThings>?, response: Response<FiveThings>) {
                if (response.isSuccessful) {
                    fiveThings.saved = true
                    fiveThingsData.value = Resource(Status.SUCCESS, "Saved!", fiveThings)
                } else {
                    fiveThingsData.value = Resource(Status.ERROR, response.message(), response.body())
                }
            }

            override fun onFailure(call: Call<FiveThings>?, t: Throwable?) {
                //TODO
                Log.d("blerg", "on failure babyyyy")
                t?.printStackTrace()
            }
        })
    }

    override fun getWrittenDates(): MutableLiveData<Resource<List<Date>>> {
        Log.d("blerg", "about to make request for written days")
        val fiveThingsDates = MutableLiveData<Resource<List<Date>>>()

        val call = fiveThingsService.getWrittenDates()
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                    fiveThingsDates.value = Resource(Status.SUCCESS, "", days)
                } else {
                    fiveThingsDates.value = Resource(Status.ERROR, response.message(), null)
                }
            }

            override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                //TODO
                Log.d("blerg", "on failure babyyyy")
                t?.printStackTrace()
            }
        })

        return fiveThingsDates

    }

}
