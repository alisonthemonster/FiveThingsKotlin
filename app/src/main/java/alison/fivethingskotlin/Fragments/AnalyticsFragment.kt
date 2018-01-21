package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.R
import alison.fivethingskotlin.ViewModels.AnalyticsViewModel
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AnalyticsFragment : Fragment() {

    private var viewModel: AnalyticsViewModel = AnalyticsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.analytics_fragment, container, false)
    }

}
