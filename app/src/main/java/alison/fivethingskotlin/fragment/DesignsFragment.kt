package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.R
import alison.fivethingskotlin.adapter.FiveThingsAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.designs_fragment.*


class DesignsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.designs_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = FiveThingsAdapter(fragmentManager!!)
        pager.currentItem = 25
    }
}