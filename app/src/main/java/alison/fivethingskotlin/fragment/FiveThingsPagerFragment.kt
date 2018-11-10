package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.R
import alison.fivethingskotlin.adapter.FiveThingsAdapter
import alison.fivethingskotlin.adapter.FiveThingsAdapter.Companion.STARTING_DAY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.container_five_things.*

//swaps in and out different five things fragments
class FiveThingsPagerFragment : Fragment() {

    companion object {

        const val INDEX = "index_key"
        var index: Int? = null

        fun newInstance(itemIndex: Int): FiveThingsPagerFragment {
            val fragment = FiveThingsPagerFragment()

            val bundle = Bundle()
            bundle.putInt(INDEX, itemIndex)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        index = arguments?.getInt(INDEX)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.container_five_things, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        five_things_pager.adapter = FiveThingsAdapter(fragmentManager!!)
        if (index != null) {
            five_things_pager.currentItem = index as Int
        } else {
            five_things_pager.currentItem = STARTING_DAY
        }
    }
}