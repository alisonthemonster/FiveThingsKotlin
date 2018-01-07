package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.R
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader


class DesignsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.designs_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        loadImages()

    }

    private fun loadImages() {
        val storage = FirebaseStorage.getInstance()
        val coverImages = storage.reference.child("CoverDesigns")

        val recyclerView = view?.findViewById<RecyclerView>(R.id.imagegallery)
        recyclerView?.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = layoutManager
        val imageNames = getImageNames()
        val adapter = GalleryAdapter(context, getImageNames())
        recyclerView?.adapter = adapter
    }

    private fun getImageNames(): List<String> {
        return listOf(
                "AprJuneFloral.png",
                "cover2017black.png",
                "Diagonal2017.png",
                "Floral2017.png",
                "JanMarchFloral.jpg",
                "JanMarchLetters.png",
                "JulyAugSepFloral.png")
    }

}

class GalleryAdapter: RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    private lateinit var imageNames: Array<String>
    private lateinit var mContext: Context
    private lateinit var storage: FirebaseStorage

    fun GalleryAdapter(context: Context, imagesList: Array<SpacePhoto>) {
        mContext = context
        imageNames = imagesList
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.MyViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.item_photo, parent, false)
        val viewHolder = GalleryAdapter.MyViewHolder(photoView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image = imageNames[position]
        val imageView = holder.itemView as ImageView
        val storageReference = storage.reference.child("CoverDesigns").child(image)

        Glide.with(mContext)
                .using(FirebaseImageLoader())
                .load(storageReference)
                .into(imageView)
    }

    override fun getItemCount(): Int {
        return imageNames.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var mPhotoImageView: ImageView

        fun MyViewHolder(itemView: View) {

            mPhotoImageView = itemView.findViewById<View>(R.id.iv_photo) as ImageView
            itemView.setOnClickListener(this)
        }
    }
}
