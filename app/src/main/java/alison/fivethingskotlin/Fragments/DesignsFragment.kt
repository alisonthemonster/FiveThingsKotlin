package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.Fragments.GalleryAdapter.MyViewHolder
import alison.fivethingskotlin.R
import alison.fivethingskotlin.ViewModels.DesignsViewModel
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage


class DesignsFragment : Fragment() {

    lateinit var viewModel: DesignsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = DesignsViewModel()

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

class ImageDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}

class GalleryAdapter(images: ArrayList<String>): RecyclerView.Adapter<MyViewHolder>() {

    private var imageNames: ArrayList<String> = images
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.template_image, parent, false)
        val viewHolder = MyViewHolder(photoView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image = imageNames[position]
        val imageView = holder.itemView as ImageView
        val storageReference = storage.reference.child("CoverDesigns").child(image)

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(storageReference)
                .into(imageView)
    }

    override fun getItemCount(): Int {
        return imageNames.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var mPhotoImageView: ImageView = itemView.findViewById<View>(R.id.design_photo) as ImageView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
           Toast.makeText(context, "ya clicked one yay", Toast.LENGTH_SHORT).show()
        }
    }


}
