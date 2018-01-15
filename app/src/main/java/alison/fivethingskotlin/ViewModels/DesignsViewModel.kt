package alison.fivethingskotlin.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class DesignsViewModel: ViewModel() {

    private var database = FirebaseDatabase.getInstance().reference
    private val imageNames = MutableLiveData<ArrayList<String>>()


    fun getDesignImageResources(): LiveData<ArrayList<String>> {
        val query = database.child("resources")
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("designs", p0.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val results= dataSnapshot.value
                if (results != null) {
                    imageNames.value = results as ArrayList<String>
                }

            }
        })
        return imageNames
    }

}