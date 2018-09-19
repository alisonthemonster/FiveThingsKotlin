package alison.fivethingskotlin.adapter

import alison.fivethingskotlin.fragment.IntroFragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


class IntroAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
         return IntroFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return 3
    }

}