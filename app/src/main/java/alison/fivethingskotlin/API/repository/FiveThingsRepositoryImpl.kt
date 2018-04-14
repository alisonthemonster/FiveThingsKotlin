package alison.fivethingskotlin.API.repository

import alison.fivethingskotlin.API.FiveThingsService
import alison.fivethingskotlin.Models.*
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.buildErrorResource
import alison.fivethingskotlin.Util.getDatabaseStyleDate
import alison.fivethingskotlin.Util.getDateFromDatabaseStyle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class FiveThingsRepositoryImpl(private val fiveThingsService: FiveThingsService = FiveThingsService.create()): FiveThingsRepository {

    override fun getFiveThings(token:String, date: Date, fiveThingsData: MutableLiveData<Resource<FiveThingz>>): LiveData<Resource<FiveThingz>> {
        val dateString = getDatabaseStyleDate(date)
        val call = fiveThingsService.getFiveThings(token, dateString)
        call.enqueue(object : Callback<FiveThingz> {
            override fun onResponse(call: Call<FiveThingz>?, response: Response<FiveThingz>) {
                Log.d("blerg", "given date: " + date)
                Log.d("blerg", "gotten date: " + response.body()?.date)

                Log.d("blerg", "responseee: " + response.body())
                Log.d("blerg", "is success: " + response.isSuccessful)

                if (response.isSuccessful) {
                    fiveThingsData.value = Resource(Status.SUCCESS, "", response.body())
                } else {
                    if (response.code() == 404) {
                        val things = FiveThingz( date, listOf("", "", "", "", ""),false)
                        fiveThingsData.value = Resource(Status.SUCCESS, "Unwritten Day", things)
                    } else {
                        fiveThingsData.value = buildErrorResource(response)
                    }
                }
            }

            override fun onFailure(call: Call<FiveThingz>?, t: Throwable?) {
                fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
            }
        })
        return fiveThingsData
    }

    override fun saveFiveThings(token:String, fiveThings: FiveThingz, fiveThingsData: MutableLiveData<Resource<FiveThingz>>) {
        Log.d("blerg", "about to make request to save day")

        if (fiveThings.isEmpty) {
            val call = fiveThingsService.deleteFiveThings(token, getDatabaseStyleDate(fiveThings.date))
            call.enqueue(object : Callback<Response<Void>> {

                override fun onResponse(call: Call<Response<Void>>?, response: Response<Response<Void>>) {
                    if (response.isSuccessful) {
                        //TODO somehow find a way to tell client to get new list of dates written
                            //what if the success response returned the list of all dates?
                    } else {
                        val json = JSONObject(response.errorBody()?.string())
                        val messageString = json.getString("message")
                        fiveThingsData.value = Resource(Status.ERROR, messageString, null)
                    }
                }

                override fun onFailure(call: Call<Response<Void>>?, t: Throwable?) {
                    fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
                }
            })
        } else {
            val things = arrayOf(fiveThings.things[0], fiveThings.things[1], fiveThings.things[2], fiveThings.things[3], fiveThings.things[4])
            val requestBody = FiveThingsRequest(getDatabaseStyleDate(fiveThings.date), things)

            if (fiveThings.saved) {
                val call = fiveThingsService.updateFiveThings(token, requestBody)
                call.enqueue(object : Callback<Message> {
                    override fun onResponse(call: Call<Message>?, response: Response<Message>) {
                        if (response.isSuccessful) {
                            fiveThings.saved = true
                            fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                        } else {
                            val json = JSONObject(response.errorBody()?.string())
                            val messageString = json.getString("message")
                            fiveThingsData.value = Resource(Status.ERROR, messageString, null)
                        }
                    }

                    override fun onFailure(call: Call<Message>?, t: Throwable?) {
                        fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
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
                            val json = JSONObject(response.errorBody()?.string())
                            val messageString = json.getString("message")
                            fiveThingsData.value = Resource(Status.ERROR, messageString, null)
                        }
                    }

                    override fun onFailure(call: Call<Message>?, t: Throwable?) {
                        fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
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
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("message")
                    fiveThingsDates.value = Resource(Status.ERROR, messageString, null)
                }
            }

            override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                fiveThingsDates.value = Resource(Status.ERROR, t?.message, null)
            }
        })

        return fiveThingsDates
    }

}
