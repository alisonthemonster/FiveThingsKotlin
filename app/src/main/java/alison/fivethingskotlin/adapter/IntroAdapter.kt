package alison.fivethingskotlin.adapter

import alison.fivethingskotlin.fragment.IntroFragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class IntroAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
         return IntroFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return 3
    }

}