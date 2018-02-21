package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.*
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

    override fun getFiveThings(token:String, date: Date, fiveThingsData: MutableLiveData<Resource<FiveThings>>): LiveData<Resource<FiveThings>> {
        val dateString = getDatabaseStyleDate(date)
        val call = fiveThingsService.getFiveThings(token, dateString)
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

    override fun saveFiveThings(token:String, fiveThings: FiveThings, fiveThingsData: MutableLiveData<Resource<FiveThings>>) {
        Log.d("blerg", "about to make request to save day")

        if (fiveThings.isEmpty) {
            val call = fiveThingsService.deleteFiveThings(token, getDatabaseStyleDate(fiveThings.date))
            call.enqueue(object : Callback<Response<Void>> {

                override fun onResponse(call: Call<Response<Void>>?, response: Response<Response<Void>>) {
                    if (response.isSuccessful) {
                        //TODO somehow find a way to tell client to get new list of dates written
                    } else {
                        //TODO
                    }
                }

                override fun onFailure(call: Call<Response<Void>>?, t: Throwable?) {
                    //TODO
                    Log.d("blerg", "on failure babyyyy")
                    t?.printStackTrace()
                }
            })
        } else {
            val requestBody = FiveThingsRequest(getDatabaseStyleDate(fiveThings.date),
                    arrayOf(fiveThings.one, fiveThings.two, fiveThings.three, fiveThings.four, fiveThings.five))

            if (fiveThings.saved) {
                val call = fiveThingsService.updateFiveThings(token, requestBody)
                call.enqueue(object : Callback<Message> {
                    override fun onResponse(call: Call<Message>?, response: Response<Message>) {
                        if (response.isSuccessful) {
                            fiveThings.saved = true
                            fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                        } else {
                            //TODO
                        }
                    }

                    override fun onFailure(call: Call<Message>?, t: Throwable?) {
                        //TODO
                        Log.d("blerg", "on failure babyyyy")
                        t?.printStackTrace()
                    }
                })
            } else {
                val call = fiveThingsService.writeFiveThings(token, requestBody)
                call.enqueue(object : Callback<Message> {
                    override fun onResponse(call: Call<Message>?, response: Response<Message>) {
                        if (response.isSuccessful) {
                            fiveThings.saved = true
                            fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                        } else {
                            //TODO
                        }
                    }

                    override fun onFailure(call: Call<Message>?, t: Throwable?) {
                        //TODO
                        Log.d("blerg", "on failure babyyyy")
                        t?.printStackTrace()
                    }
                })
            }
        }
    }

    override fun getWrittenDates(token: String): MutableLiveData<Resource<List<Date>>> {
        Log.d("blerg", "about to make request for written days")
        val fiveThingsDates = MutableLiveData<Resource<List<Date>>>()

        val call = fiveThingsService.getWrittenDates(token)
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
