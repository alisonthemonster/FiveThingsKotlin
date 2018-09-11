package alison.fivethingskotlin.adapter

import alison.fivethingskotlin.fragment.FiveThingsFragment
import alison.fivethingskotlin.util.getFullDateFormat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import java.util.*


class FiveThingsAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        const val INFINITY = 1000
        const val STARTING_DAY = 500
    }

    override fun getCount(): Int {
        return INFINITY
    }

    override fun getItem(position: Int): Fragment {
        //position 500 is todays date
        //position is the number of days to add to today's date

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, position - STARTING_DAY)

        val dateString = getFullDateFormat(cal.time)

        return FiveThingsFragment.newInstance(dateString)
    }
}