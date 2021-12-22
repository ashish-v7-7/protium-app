/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.protium.ashish.R
import com.protium.ashish.activities.MapActivity
import com.protium.ashish.adapters.ExplorePostAdapter
import com.protium.ashish.data.ExplorePost
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExploreFragment : Fragment() {

    private var exploreMap: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private var explorePostAdapter: ExplorePostAdapter? = null
    private var explorePostList: MutableList<ExplorePost>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        recyclerView = view.findViewById(R.id.explore_recycler_view)
        exploreMap = view.findViewById(R.id.explore_map)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

        explorePostList = ArrayList()
        explorePostAdapter = context?.let {
            ExplorePostAdapter({ it2 ->
                moreMenuDialog(it2)
            }, it, explorePostList as ArrayList<ExplorePost>)
        }
        recyclerView!!.adapter = explorePostAdapter

        exploreMap!!.setOnClickListener {

            val i = Intent(context, MapActivity::class.java)
            i.putExtra("isComingFromExplore", true)
            startActivity(i)
        }
        retrievePosts()
    }

    @SuppressLint("SimpleDateFormat")
    private fun moreMenuDialog(explorePost: ExplorePost) {
        val detailBoxView2 =
            LayoutInflater.from(context).inflate(R.layout.more_menu_explore_post, null)
        val detailsBoxBuilder2 = context?.let { AlertDialog.Builder(it).setView(detailBoxView2) }


        val detailsButton: TextView = detailBoxView2.findViewById(R.id.more_menu_details)
        val shareButton: TextView = detailBoxView2.findViewById(R.id.more_menu_share)
        val saveButton: TextView = detailBoxView2.findViewById(R.id.more_menu_save)
        val CloseButton: TextView = detailBoxView2.findViewById(R.id.more_menu_close)

        detailsButton.setOnClickListener {
            val detailBoxView =
                LayoutInflater.from(context).inflate(R.layout.custom_layout_post_details, null)
            val detailsBoxBuilder = android.app.AlertDialog.Builder(context).setView(detailBoxView)

            val publisherName: TextView =
                detailBoxView.findViewById(R.id.custom_layout_post_details_publisher_name)

            val userReference = FirebaseDatabase.getInstance().reference.child("user").child(
                explorePost.publisher
            )


            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.child("name").value
                        publisherName.text = user.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    publisherName.text = "Not Found"
                }
            })


            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(explorePost.timeStamp)
            val date = sdf.format(netDate)

            Log.e("Tag", "Formatted Date" + date)
            detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_created_at).text =
                date
            detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_location).text =
                explorePost.district + ", " + explorePost.state
            detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_latitude).text =
                explorePost.latitude.toString()
            detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_longitude).text =
                explorePost.longitude.toString()
            val url: TextView =
                detailBoxView.findViewById(R.id.custom_layout_post_details_image_url)
            url.text = explorePost.postImage
            url.setOnClickListener {
                //launch chrome
                try {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(explorePost.postImage)).apply {
                            // The URL should either launch directly in a non-browser app
                            // (if it’s the default), or in the disambiguation dialog
                            addCategory(Intent.CATEGORY_BROWSABLE)

                        }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Browser not supported", Toast.LENGTH_LONG).show()

                }
            }
            var tagText = ""
            for (tag in explorePost.postTag) {
                tagText = tagText + "#" + tag + " "
            }
            detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_tag).text = tagText


            val alertDialog2 = detailsBoxBuilder.show()
            detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_close_btn)
                .setOnClickListener {
                    alertDialog2.dismiss()
                }
        }
        shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, explorePost.postImage.toString())
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }
        saveButton.setOnClickListener {
            //launch chrome
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(explorePost.postImage)).apply {

                    addCategory(Intent.CATEGORY_BROWSABLE)

                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Browser not supported", Toast.LENGTH_LONG).show()

            }
        }

        val alertDialog = detailsBoxBuilder2?.show()
        CloseButton.setOnClickListener {
            alertDialog?.dismiss()
        }
    }

    private fun retrievePosts() {
        val postPref = FirebaseDatabase.getInstance().reference.child("post")

        postPref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                explorePostList?.clear()
                for (snap in snapshot.children) {

                    //val post = snap.getValue(Post::class.java)
                    val postId = snap.child("postId").value
                    val postImage = snap.child("postImage").value
                    val postTag = snap.child("postTag").value
                    val timeStamp = snap.child("timeStamp").value
                    val latitude = snap.child("latitude").value
                    val longitude = snap.child("longitude").value
                    val state = snap.child("state").value
                    val district = snap.child("district").value
                    val publisher = snap.child("publisher").value
                    val isDeleted = snap.child("isDeleted").value

                    Log.e("postId", postId.toString())
                    Log.e("postImage", postImage.toString())
                    Log.e("postTag", postTag.toString())
                    Log.e("timeStamp", timeStamp.toString())
                    Log.e("latitude", latitude.toString())
                    Log.e("longitude", longitude.toString())
                    Log.e("country", state.toString())
                    Log.e("district", district.toString())
                    Log.e("publisher", publisher.toString())
                    Log.e("isDeleted", isDeleted.toString())
                    //postList!!.add(post!!)
                    val PostId: String = postId.toString()
                    val PostImage = postImage as String
                    val PostTag = postTag as ArrayList<String>
                    val TimeStamp = timeStamp as Long
                    val Latitude = latitude as Double
                    val Longitude = longitude as Double
                    val State = state as String
                    val District = district as String
                    val Publisher = publisher as String
                    val IsDeleted = isDeleted as Boolean

                    explorePostList!!.add(
                        ExplorePost(
                            PostId,
                            PostImage,
                            PostTag,
                            TimeStamp,
                            Latitude,
                            Longitude,
                            State,
                            District,
                            Publisher,
                            IsDeleted
                        )
                    )

                }
                explorePostAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}