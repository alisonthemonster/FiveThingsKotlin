package alison.fivethingskotlin.fivethings

import alison.fivethingskotlin.util.getFullDateFormat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import java.util.*


class FiveThingsAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        const val PAGER_SIZE = 10000 //user can see data from any day ~13years away from today
        const val STARTING_DAY = PAGER_SIZE /2
    }

    override fun getCount(): Int {
        return PAGER_SIZE
    }

    override fun getItem(position: Int): Fragment {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, position - STARTING_DAY)

        val dateString = getFullDateFormat(cal.time)

        return FiveThingsFragment.newInstance(dateString)
    }
}