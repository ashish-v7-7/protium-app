/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.protium.ashish.R
import com.protium.ashish.activities.AddImageActivity
import com.protium.ashish.activities.MapActivity
import com.protium.ashish.activities.ViewImageDetailsActivity
import com.protium.ashish.adapters.PostAdapter
import com.protium.ashish.data.Post

class HomeFragment : Fragment() {

    private var addButton: ImageView? = null
    private var textViewNoPost: TextView? = null
    var ivMap: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var posList = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        addButton = view.findViewById(R.id.home_add)
        ivMap = view.findViewById(R.id.home_map)
        textViewNoPost = view.findViewById(R.id.home_textView)

        recyclerView = view.findViewById(R.id.home_recycler_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addButton!!.setOnClickListener {
            val intent = Intent(context, AddImageActivity::class.java)
            startActivity(intent)
        }
        ivMap!!.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("isComingFromExplore", false)
            startActivity(i)
        }

        recyclerView!!.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        postAdapter = context?.let {
            PostAdapter({ it2 ->
                Log.d("item", it2.postId)
                val intent = Intent(context, ViewImageDetailsActivity::class.java)
                intent.putExtra("postId", it2.postId)
                startActivity(intent)
            }, it, posList)
        }
        //postAdapter!!.onItemClick =
        recyclerView!!.adapter = postAdapter

        retrievePosts()
    }

    private fun retrievePosts() {
        val progressDialog = showProgressDialog()
        val postPref =
            FirebaseDatabase.getInstance().reference.child("post").orderByChild("publisher")
                .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)

        postPref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posList.clear()
                for (snap in snapshot.children) {

                    val postId = snap.child("postId").value
                    val postImage = snap.child("postImage").value
                    val publisher = snap.child("publisher").value

                    val PostId: String = postId.toString()
                    val PostImage = postImage as String
                    val Publisher = publisher as String

                    posList.add(
                        Post(
                            PostId, PostImage, Publisher
                        )
                    )

                }
                textViewNoPost!!.isVisible = posList.size == 0
                progressDialog?.dismiss()
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showProgressDialog(): AlertDialog? {
        val detailBoxView =
            LayoutInflater.from(context).inflate(R.layout.custom_layout_progress_dialog, null)
        val detailsBoxBuilder = context?.let { AlertDialog.Builder(it).setView(detailBoxView) }
        detailsBoxBuilder!!.setCancelable(false)

        return detailsBoxBuilder.show()
    }
}