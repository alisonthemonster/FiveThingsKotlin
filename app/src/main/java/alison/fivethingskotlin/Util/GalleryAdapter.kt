package alison.fivethingskotlin.Util

import alison.fivethingskotlin.R
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage

class GalleryAdapter(images: ArrayList<String>): RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    private var imageNames: ArrayList<String> = images
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.template_image, parent, false)
        return MyViewHolder(photoView)
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

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Toast.makeText(context, "Feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }


}
