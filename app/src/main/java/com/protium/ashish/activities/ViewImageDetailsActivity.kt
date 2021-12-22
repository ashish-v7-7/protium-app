/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.CATEGORY_BROWSABLE
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.protium.ashish.R
import com.protium.ashish.data.ExplorePost
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ViewImageDetailsActivity : AppCompatActivity() {

    private var ivMap: ImageView? = null
    private var ivImageView: ImageView? = null
    private var ivImageViewBack: TextView? = null
    private var tvDetails: TextView? = null
    private var tvEdit: TextView? = null
    private var tvDelete: TextView? = null
    private var tvShare: TextView? = null

    private var explorePost: ExplorePost? = null
    private var postId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image_details)


        postId = intent.getStringExtra("postId").toString()

        getPost(postId!!)

        ivMap = findViewById(R.id.activity_view_image_map)
        ivImageView = findViewById(R.id.activity_view_image_imageView)
        ivImageViewBack = findViewById(R.id.activity_view_image_back)
        tvDetails = findViewById(R.id.activity_view_image_details)
        tvEdit = findViewById(R.id.activity_view_image_edit)
        tvDelete = findViewById(R.id.activity_view_image_delete)
        tvShare = findViewById(R.id.activity_view_image_share)



        ivImageViewBack!!.setOnClickListener {
            finish()
        }
        tvDetails!!.setOnClickListener {
            showDialog()

        }
        tvEdit!!.setOnClickListener {
            val i = Intent(this, AddImageActivity::class.java)
            i.putExtra("postId", postId)

            i.putExtra("isUpdatePost", true)

            startActivity(i)

        }
        tvDelete!!.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure ?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Yes") { dialogInterface, which ->
                dialogInterface.dismiss()
                delete()
            }

            builder.setNegativeButton("Cancel") { dialogInterface, which ->

            }
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
//


        }
        tvShare!!.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, explorePost!!.postImage.toString())
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, "Share To"))
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun showDialog() {
        val detailBoxView =
            LayoutInflater.from(this).inflate(R.layout.custom_layout_post_details, null)
        val detailsBoxBuilder = AlertDialog.Builder(this).setView(detailBoxView)

        val publisherName: TextView =
            detailBoxView.findViewById(R.id.custom_layout_post_details_publisher_name)

        val userReference =
            FirebaseDatabase.getInstance().reference.child("user").child(explorePost!!.publisher)


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
        val netDate = Date(explorePost!!.timeStamp)
        val date = sdf.format(netDate)

        Log.e("Tag", "Formatted Date" + date)
        detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_created_at).text = date
        detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_location).text =
            explorePost!!.district + ", " + explorePost!!.state
        detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_latitude).text =
            explorePost!!.latitude.toString()
        detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_longitude).text =
            explorePost!!.longitude.toString()
        val url: TextView = detailBoxView.findViewById(R.id.custom_layout_post_details_image_url)
        url.text = explorePost!!.postImage
        url.setOnClickListener {
            //launch chrome
            try {
                val intent = Intent(
                    android.content.Intent.ACTION_VIEW,
                    Uri.parse(explorePost!!.postImage)
                ).apply {
                    addCategory(CATEGORY_BROWSABLE)

                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Browser not supported", Toast.LENGTH_LONG).show()

            }
        }
        var tagText = ""
        for (tag in explorePost!!.postTag) {
            tagText = tagText + "#" + tag + " "
        }
        detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_tag).text = tagText


        val alertDialog = detailsBoxBuilder.show()
        detailBoxView.findViewById<TextView>(R.id.custom_layout_post_details_close_btn)
            .setOnClickListener {
                alertDialog.dismiss()
            }
    }

    private fun getPost(postId: String) {
        val postReference = FirebaseDatabase.getInstance().reference.child("post").child(postId)

        postReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val postId = snapshot.child("postId").value
                    val postImage = snapshot.child("postImage").value
                    val postTag = snapshot.child("postTag").value
                    val timeStamp = snapshot.child("timeStamp").value
                    val latitude = snapshot.child("latitude").value
                    val longitude = snapshot.child("longitude").value
                    val state = snapshot.child("state").value
                    val district = snapshot.child("district").value
                    val publisher = snapshot.child("publisher").value
                    val isDeleted = snapshot.child("isDeleted").value

                    val PostId = postId as String
                    val PostImage = postImage as String
                    val PostTag = postTag as ArrayList<String>
                    val TimeStamp = timeStamp as Long
                    val Latitude = latitude as Double
                    val Longitude = longitude as Double
                    val State = state as String
                    val District = district as String
                    val Publisher = publisher as String
                    val IsDeleted = isDeleted as Boolean


                    explorePost = ExplorePost(
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
                    Picasso.get().load(explorePost!!.postImage).into(ivImageView)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun delete() {
        val imgRef = explorePost!!.timeStamp
        val postReference =
            FirebaseDatabase.getInstance().reference.child("post").child(explorePost!!.postId)
                .removeValue()

        postReference.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val storageRef = FirebaseStorage.getInstance().reference.child("post_image")
                    .child(imgRef.toString() + ".jpg").delete()

                storageRef.addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        finish()
                    }
                }
                Toast.makeText(this, "Delete Success", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "Delete Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}