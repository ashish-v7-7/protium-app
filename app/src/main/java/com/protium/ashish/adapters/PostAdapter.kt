/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.protium.ashish.R
import com.protium.ashish.data.Post
import com.squareup.picasso.Picasso

class PostAdapter(
    var onItemClick: ((Post) -> Unit)? = null,
    private var mCtx: Context,
    private var postList: List<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        Picasso.get().load(post.postImage).into(holder.image)

    }


    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.item_image_imageview)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(postList[adapterPosition])
            }
        }
    }
}