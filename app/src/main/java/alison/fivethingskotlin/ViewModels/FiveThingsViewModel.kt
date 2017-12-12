package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java.text.SimpleDateFormat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class FiveThingsViewModel(): ViewModel() {

//    class FiveThingsViewModel(
//            private val user: FirebaseUser): ViewModel() {

//    private var database = FirebaseDatabase.getInstance().reference
    private val fiveThingsData = MutableLiveData<FiveThings>()

    fun getFiveThings(date: Date): LiveData<FiveThings> {
        //TODO utils to get date into string format
        val formattedDate = SimpleDateFormat("MM-dd-yy").format(date).toString()

        fiveThingsData.value = FiveThings(Date(), "Hello", "Hello","Hello","Hello","Hello")

        //TODO commented out until OAuth Token problem is fixed
//        val dateQuery = database.child("users").child(user.uid).child(formattedDate)
//        dateQuery.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError?) {
//                //TODO
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val results = dataSnapshot.value as ArrayList<String>
//                var fiveThings = FiveThings(date, "","","", "", "")
//                if (results != null) {
//                    fiveThings = FiveThings(date,
//                            results[0],
//                            results[1],
//                            results[2],
//                            results[3],
//                            results[4])
//                }
//                //TODO somehow put the fiveThings into the fiveThingsData
//                fiveThingsData.value = fiveThings
//            }
//        })
        return fiveThingsData
    }

    fun writeFiveThings(user: FirebaseUser, fiveThings: FiveThings) {
        val things = ArrayList<String>()
        things.add(fiveThings.one)
        things.add(fiveThings.two)
        things.add(fiveThings.three)
        things.add(fiveThings.four)
        things.add(fiveThings.five)

        val formattedDate = SimpleDateFormat("MM-dd-yy").format(fiveThings.date).toString()
        //TODO use date util to convert to string

//        database.child("users").child(user.uid).child(formattedDate).setValue(things) { error, ref ->
//            println("Value was set. Error = " + error)
//        }
    }
}