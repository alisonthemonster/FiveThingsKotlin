package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.Observer
import alison.fivethingskotlin.R
import alison.fivethingskotlin.ViewModels.FiveThingsViewModel
import alison.fivethingskotlin.databinding.FiveThingsFragmentBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class FiveThingsFragment : Fragment() {

    var currentDate = Date()
//    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

//        if (user != null) {
            val viewModel = FiveThingsViewModel()

            val binding = FiveThingsFragmentBinding.inflate(inflater!!, container, false)
            binding.viewModel = viewModel

            viewModel.getFiveThings(currentDate).observe(this, Observer<FiveThings> { fiveThings ->
                binding.fiveThings = fiveThings
            })

            return binding.root
//        }
        //TODO handle case where user get here without logging in
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.five_things_fragment, container, false)
    }
}
