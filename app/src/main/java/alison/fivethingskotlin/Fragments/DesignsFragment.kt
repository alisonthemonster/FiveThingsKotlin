package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.GalleryAdapter
import alison.fivethingskotlin.Util.ImageDecoration
import alison.fivethingskotlin.ViewModels.DesignsViewModel
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class DesignsFragment : Fragment() {

    var viewModel: DesignsViewModel = DesignsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel.getDesignImageResources().observe(this, Observer<ArrayList<String>> { imageNames ->
            imageNames?.let {
                loadImages(imageNames)
            }
        })

        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.designs_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
    }

    private fun loadImages(imageNames: ArrayList<String>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.imagegallery)
        recyclerView?.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = layoutManager
        val adapter = GalleryAdapter(imageNames)
        recyclerView?.adapter = adapter
        val spanCount = 2
        val spacing = 30
        val includeEdge = false
        recyclerView?.addItemDecoration(ImageDecoration(spanCount, spacing, includeEdge))
    }
}
