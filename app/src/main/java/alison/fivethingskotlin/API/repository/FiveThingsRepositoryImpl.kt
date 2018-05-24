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

    override fun getFiveThings(token:String, date: Date, fiveThingsData: MutableLiveData<Resource<FiveThings>>): LiveData<Resource<FiveThings>> {
        val dateString = getDatabaseStyleDate(date)
        val call = fiveThingsService.getFiveThings(token, dateString)
        call.enqueue(object : Callback<FiveThings> {
            override fun onResponse(call: Call<FiveThings>?, response: Response<FiveThings>) {
                if (response.isSuccessful) {
                    if (!response.body()!!.isEmpty) {
                        //if there is data from DB then we know its inDatabase
                        response.body()?.inDatabase = true
                    }
                    fiveThingsData.value = Resource(Status.SUCCESS, "", response.body())
                } else {
                    if (response.code() == 404) {
                        val things = FiveThings(date, listOf("", "", "", "", ""),false, false)
                        fiveThingsData.value = Resource(Status.SUCCESS, "Unwritten Day", things)
                    } else {
                        fiveThingsData.value = buildErrorResource(response)
                    }
                }
            }

            override fun onFailure(call: Call<FiveThings>?, t: Throwable?) {
                fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
            }
        })
        return fiveThingsData
    }

    override fun saveFiveThings(token:String, fiveThings: FiveThings, fiveThingsData: MutableLiveData<Resource<FiveThings>>): MutableLiveData<Resource<List<Date>>> {
        val writtenDates = MutableLiveData<Resource<List<Date>>>()

        if (fiveThings.isEmpty) {
            //DELETE AN ENTRY
            val call = fiveThingsService.deleteFiveThings(token, FiveThingsRequest(getDatabaseStyleDate(fiveThings.date)))
                call.enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                        writtenDates.value = Resource(Status.SUCCESS, "Date removed", days)
                    } else {
                        writtenDates.value = Resource(Status.ERROR, "", null)
                        //TODO
//                        response.errorBody()?.let {
//                                val json = JSONObject(response.errorBody()?.string())
//                                val messageString = json.getString("message")
//                                writtenDates.value = Resource(Status.ERROR, messageString, null)
//                        }
                    }
                }

                override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                    writtenDates.value = Resource(Status.ERROR, t?.message, null)
                }
            })
        } else {
            val things = arrayOf(fiveThings.things[0], fiveThings.things[1], fiveThings.things[2], fiveThings.things[3], fiveThings.things[4])
            val requestBody = FiveThingsRequest(getDatabaseStyleDate(fiveThings.date), things)

            if (fiveThings.inDatabase) {
                //UPDATE AN ALREADY WRITTEN DAY
                val call = fiveThingsService.updateFiveThings(token, requestBody)
                call.enqueue(object : Callback<List<String>> {
                    override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                        if (response.isSuccessful) {
                            fiveThings.edited = false
                            fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                            val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                            writtenDates.value = Resource(Status.SUCCESS, "Date updated", days)
                        } else {
//                            val json = JSONObject(response.errorBody()?.string())
//                            val messageString = json.getString("message")
//                            writtenDates.value = Resource(Status.ERROR, messageString, null)
                            //TODO
                            writtenDates.value = Resource(Status.ERROR, "", null)

                        }
                    }

                    override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                        writtenDates.value = Resource(Status.ERROR, t?.message, null)
                    }
                })
            } else {
                //A BRAND NEW DAY TO BE SAVED
                val call = fiveThingsService.writeFiveThings(token, requestBody)
                call.enqueue(object : Callback<List<String>> {
                    override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                        if (response.isSuccessful) {
                            fiveThings.inDatabase = true
                            fiveThings.edited = false
                            fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                            val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                            writtenDates.value = Resource(Status.SUCCESS, "Date in database", days)
                        } else {
                            //val json = JSONObject(response.errorBody()?.string())
                            //val messageString = json.getString("message")
                            //TODO
                            writtenDates.value = Resource(Status.ERROR, "", null)

                            //writtenDates.value = Resource(Status.ERROR, messageString, null)
                        }
                    }

                    override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                        fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
                    }
                })
            }
        }
        return writtenDates
    }

    override fun getWrittenDates(token: String): MutableLiveData<Resource<List<Date>>> {
        val fiveThingsDates = MutableLiveData<Resource<List<Date>>>()

        val call = fiveThingsService.getWrittenDates(token)
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                    fiveThingsDates.value = Resource(Status.SUCCESS, "", days)
                } else {
                    //val json = JSONObject(response.errorBody()?.string())
                    //val messageString = json.getString("message")
                    //fiveThingsDates.value = Resource(Status.ERROR, messageString, null)
                    //TODO
                    fiveThingsDates.value = Resource(Status.ERROR, "", null)
                }
            }

            override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                fiveThingsDates.value = Resource(Status.ERROR, t?.message, null)
            }
        })

        return fiveThingsDates
    }

}
