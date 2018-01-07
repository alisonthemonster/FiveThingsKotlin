package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.getDatabaseStyleDate
import alison.fivethingskotlin.Util.getDateFromDatabaseStyle
import alison.fivethingskotlin.Util.getNextDate
import alison.fivethingskotlin.Util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class FiveThingsViewModel(private val user: FirebaseUser): ViewModel() {

    private var database = FirebaseDatabase.getInstance().reference
    private val fiveThingsData = MutableLiveData<FiveThings>()
    private val fiveThingsDates = MutableLiveData<List<Date>>()

    fun getFiveThings(date: Date): LiveData<FiveThings> {
        val formattedDate = getDatabaseStyleDate(date)

        val dateQuery = database.child("users").child(user.uid).child(formattedDate)
        Log.d("fivethings", "date query: " + dateQuery)
        dateQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("fivethings", p0.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val results= dataSnapshot.value
                if (results != null) {
                    val things = results as ArrayList<String>
                    val fiveThings = FiveThings(date,
                            things[0],
                            things[1],
                            things[2],
                            things[3],
                            things[4])
                    fiveThingsData.value = fiveThings
                    Log.d("fivethings", "data set!")
                } else {
                    Log.d("fivethings", "no data found for this day")
                    fiveThingsData.value = FiveThings(date, "", "","","","")
                }
            }
        })
        return fiveThingsData
    }

    fun writeFiveThings(fiveThings: FiveThings) {
        Log.d("fivethings", "about to write the data: " + fiveThings)
        val things = ArrayList<String>()
        things.add(fiveThings.one)
        things.add(fiveThings.two)
        things.add(fiveThings.three)
        things.add(fiveThings.four)
        things.add(fiveThings.five)

        val formattedDate = getDatabaseStyleDate(fiveThings.date)

        database.child("users").child(user.uid).child(formattedDate).setValue(things) { error, ref ->
            if (error != null) {
                Log.d("fivethings", "No error: " + ref)
            }
        }
    }

    fun getPreviousDay(date: Date): LiveData<FiveThings> {
        val prevDate = getPreviousDate(date)
        return getFiveThings(prevDate)
    }

    fun getNextDay(date: Date): LiveData<FiveThings>  {
        val nextDate = getNextDate(date)
        return getFiveThings(nextDate)
    }

    fun changeDate(date: Date): LiveData<FiveThings> {
        return getFiveThings(date)
    }

    fun getWrittenDays(): LiveData<List<Date>> {
        val query = database.child("users").child(user.uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("fivethings", p0.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val results= dataSnapshot.value as Map<String, List<String>>

                Log.d("blerg", "results: " + results)
                val dayStrings = results.keys
                Log.d("blerg", "dayStrings: " + dayStrings)
                val days = dayStrings.map { getDateFromDatabaseStyle(it) }
                fiveThingsDates.value = days
            }
        })
        return fiveThingsDates
    }

}