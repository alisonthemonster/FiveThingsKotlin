package alison.fivethingskotlin.adapter

import alison.fivethingskotlin.fragment.FiveThingsFragment
import alison.fivethingskotlin.util.getFullDateFormat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import java.util.*


class FiveThingsAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 50
    }

    override fun getItem(position: Int): Fragment {
        //position 25 is todays date
        //position is the number of days to add to today's date

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, position - 25)

        val dateString = getFullDateFormat(cal.time)

        return FiveThingsFragment.newInstance(dateString)
    }
}