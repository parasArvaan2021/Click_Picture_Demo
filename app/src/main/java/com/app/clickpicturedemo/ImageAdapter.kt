package com.app.clickpicturedemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(private val context: Context, private var imageList: ArrayList<ImageModel>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageModel = imageList[position]
        holder.apply {
            Glide.with(context).load(imageModel.uri).into(imageView)
            tvItemText.text = imageModel.name
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun addNewItem(itemsNew: List<ImageModel>) {
        imageList = arrayListOf()
        imageList.addAll(itemsNew)
        notifyDataSetChanged()
    }


    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivItemImage)
        val tvItemText: TextView = itemView.findViewById(R.id.tvItemText)
    }
}