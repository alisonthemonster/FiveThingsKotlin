package alison.fivethingskotlin.api.repository

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.model.Thing
import alison.fivethingskotlin.util.*
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class FiveThingsRepositoryImpl(private val fiveThingsService: FiveThingsService = FiveThingsService.create()): FiveThingsRepository {

    override fun getFiveThings(token:String, date: Date, fiveThingsData: MutableLiveData<Resource<FiveThings>>): LiveData<Resource<FiveThings>> {
        val dateString = getDatabaseStyleDate(date)
        val call = fiveThingsService.getFiveThings(token, getYear(date).toString(),
                                                    String.format("%02d", getMonthNumber(date)),
                                                    String.format("%02d", getDay(date)))
        call.enqueue(object : Callback<List<Thing>> {
            override fun onResponse(call: Call<List<Thing>>?, response: Response<List<Thing>>) {
                if (response.isSuccessful) {
                    if (!response.body()!!.isEmpty()) {
                        //if there is data from DB then we know its inDatabase
                        fiveThingsData.value = Resource(Status.SUCCESS, "",
                                FiveThings(date, response.body()!!, false, true))
                    } else {
                        fiveThingsData.value = Resource(Status.SUCCESS, "",
                                FiveThings(date, response.body()!!, false, false))
                    }
                } else {
                    if (response.code() == 404) {
                        val things = FiveThings(date, listOf(Thing(dateString, "", 1),
                                                            Thing(dateString, "", 2),
                                                            Thing(dateString, "", 3),
                                                            Thing(dateString, "", 4),
                                                            Thing(dateString, "", 5)),
                                        false, false)
                        fiveThingsData.value = Resource(Status.SUCCESS, "Unwritten Day", things)
                    } else {
                        fiveThingsData.value = buildErrorResource(response)
                    }
                }
            }

            override fun onFailure(call: Call<List<Thing>>?, t: Throwable?) {
                fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
            }
        })
        return fiveThingsData
    }

    override fun saveFiveThings(token:String, fiveThings: FiveThings, fiveThingsData: MutableLiveData<Resource<FiveThings>>, writtenDates: MutableLiveData<Resource<List<Date>>>): MutableLiveData<Resource<List<Date>>> {

        val things = arrayOf(fiveThings.things[0], fiveThings.things[1], fiveThings.things[2], fiveThings.things[3], fiveThings.things[4])

        if (fiveThings.inDatabase) {
            //UPDATE AN ALREADY WRITTEN DAY
            val call = fiveThingsService.updateFiveThings(token, things)
            call.enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        fiveThings.edited = false
                        fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                        val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                        writtenDates.value = Resource(Status.SUCCESS, "Date updated", days)
                    } else {
                        try {
                            val messageString = if (response.code() == 500) {
                                "Internal Server Error. Hang tight we'll fix it!"
                            } else {
                                val json = JSONObject(response.errorBody()?.string())
                                json.getString("detail")
                            }
                            writtenDates.value = Resource(Status.ERROR, messageString, null)
                        } catch (e: Exception) {
                            writtenDates.value = Resource(Status.ERROR, "Unknown error occured", null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                    writtenDates.value = Resource(Status.ERROR, t?.message, null)
                }
            })
        } else {
            //A BRAND NEW DAY TO BE SAVED
            val call = fiveThingsService.writeFiveThings(token, things)
            call.enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        fiveThings.inDatabase = true
                        fiveThings.edited = false
                        fiveThingsData.value = Resource(Status.SUCCESS, response.message(), fiveThings)
                        val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                        writtenDates.value = Resource(Status.SUCCESS, "Date in database", days)
                    } else {
                        try {
                            val json = JSONObject(response.errorBody()?.string())
                            val messageString = json.getString("detail")
                            writtenDates.value = Resource(Status.ERROR, messageString, null)
                        } catch (e: Exception) {
                            //if there's malformed json
                            writtenDates.value = Resource(Status.ERROR, "", null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                    fiveThingsData.value = Resource(Status.ERROR, t?.message, null)
                }
            })
        }

        return writtenDates
    }

    override fun getWrittenDates(token: String, writtenDates: MutableLiveData<Resource<List<Date>>>): MutableLiveData<Resource<List<Date>>> {

        val call = fiveThingsService.getWrittenDates(token)
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val days = response.body()?.map { getDateFromDatabaseStyle(it) }
                    writtenDates.value = Resource(Status.SUCCESS, "", days)
                } else {
                    val json = JSONObject(response.errorBody()?.string())
                    val messageString = json.getString("detail")
                    writtenDates.value = Resource(Status.ERROR, messageString, null)
                }
            }

            override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                writtenDates.value = Resource(Status.ERROR, t?.message, null)
            }
        })

        return writtenDates
    }

}
