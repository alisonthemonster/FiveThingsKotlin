package alison.fivethingskotlin.intro

import alison.fivethingskotlin.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View


class IntroFragment : Fragment() {

    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!arguments!!.containsKey(PAGE))
            throw RuntimeException("Fragment must contain a \"$PAGE\" argument!")
        page = arguments?.getInt(PAGE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Select a layout based on the current page
        val layoutResId =  when (page) {
            0 -> R.layout.fragment_intro_one
            1 -> R.layout.fragment_intro_two
            2 -> R.layout.fragment_intro_three
            else -> R.layout.fragment_intro_one
        }

        val view = activity?.layoutInflater?.inflate(layoutResId, container, false)
        view?.tag = page

        return view
    }

    companion object {

        const val PAGE = "page"

        fun newInstance(page: Int): IntroFragment {
            val fragment = IntroFragment()
            val b = Bundle()
            b.putInt(PAGE, page)
            fragment.arguments = b
            return fragment
        }
    }

}