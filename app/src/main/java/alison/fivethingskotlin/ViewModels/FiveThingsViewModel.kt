package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.getDatabaseStyleDate
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
                    Log.d("fivethings", "results: " + results)
                    Log.d("fivethings", "things: " + things)
                    val fiveThings = FiveThings(date,
                            things[0],
                            things[1],
                            things[2],
                            things[3],
                            things[4])
                    fiveThingsData.value = fiveThings
                } else {
                    Log.d("fivethings", "no data found for this day")
                    fiveThingsData.value = FiveThings(Date(), "", "","","","")
                }
            }
        })
        return fiveThingsData
    }

    fun writeFiveThings(user: FirebaseUser, fiveThings: FiveThings) {
        val things = ArrayList<String>()
        things.add(fiveThings.one)
        things.add(fiveThings.two)
        things.add(fiveThings.three)
        things.add(fiveThings.four)
        things.add(fiveThings.five)

        val formattedDate = getDatabaseStyleDate(fiveThings.date)

        database.child("users").child(user.uid).child(formattedDate).setValue(things) { error, ref ->
            println("Value was set. Error = " + error)
        }
    }
}