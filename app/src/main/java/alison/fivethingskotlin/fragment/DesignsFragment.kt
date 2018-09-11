package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.R
import alison.fivethingskotlin.adapter.FiveThingsAdapter
import alison.fivethingskotlin.adapter.FiveThingsAdapter.Companion.STARTING_DAY
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.designs_fragment.*


class DesignsFragment : Fragment() {

    companion object {

        const val INDEX = "index_key"
        var index: Int? = null

        fun newInstance(itemIndex: Int): DesignsFragment {
            val fragment = DesignsFragment()

            val bundle = Bundle()
            bundle.putInt(INDEX, itemIndex)
            fragment.arguments = bundle

            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        index = arguments?.getInt(INDEX)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.designs_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        pager.adapter = FiveThingsAdapter(fragmentManager!!)
        if (index != null) {
            pager.currentItem = index as Int
        } else {
            pager.currentItem = STARTING_DAY
        }
    }
}