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
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.protium.ashish.R
import com.protium.ashish.data.ExplorePost
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ExplorePostAdapter(
    var onItemClick: ((ExplorePost) -> Unit)? = null,
    private val mCtx: Context,
    private val mExplorePost: List<ExplorePost>
) :
    RecyclerView.Adapter<ExplorePostAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView = itemView.findViewById(R.id.item_explore_profile_pic)
        var userFullName: TextView = itemView.findViewById(R.id.item_explore_name)
        var postAddress: TextView = itemView.findViewById(R.id.item_explore_location)
        var postTag: TextView = itemView.findViewById(R.id.item_explore_tags)
        var postImage: ImageView = itemView.findViewById(R.id.item_explore_post_image)
        var moreButton: ImageView = itemView.findViewById(R.id.item_explore_more)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.item_explore, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mExplorePost[position]
        Picasso.get().load(post.postImage).into(holder.postImage)
        holder.postAddress.text =
            post.district + ", " + post.state + " (lat: " + post.latitude + ", long: " + post.longitude + ")"
        var tag = ""
        for (t in post.postTag) {
            tag = tag + "#" + t + " "
        }
        holder.postTag.text = tag
        publisherInfo(holder.profileImage, holder.userFullName, post.publisher)
        holder.moreButton.setOnClickListener {
            onItemClick?.invoke(mExplorePost[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return mExplorePost.size
    }

    private fun publisherInfo(profileImage: ImageView, userFullName: TextView, publisher: String) {

        val userReference = FirebaseDatabase.getInstance().reference.child("user").child(publisher)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.child("name").value
                    val profilePic = snapshot.child("profilePic").value
                    userFullName.text = user.toString()
                    Picasso.get().load(profilePic.toString()).into(profileImage)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}