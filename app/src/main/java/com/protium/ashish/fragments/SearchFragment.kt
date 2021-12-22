/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.protium.ashish.R
import com.protium.ashish.adapters.PostAdapter
import com.protium.ashish.data.Post
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var posList = ArrayList<Post>()
    private var etSearch: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.search_recycler_view)
        etSearch = view.findViewById(R.id.search_edit_text)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView!!.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        postAdapter = context?.let {
            PostAdapter({ it2 ->
                Log.d("item", it2.postId)
            }, it, posList)
        }
        recyclerView!!.adapter = postAdapter

        etSearch!!.addTextChangedListener(mTextWatcher)

    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        private var timer = Timer()
        private val DELAY: Long = 200L

        override fun afterTextChanged(s: Editable?) {
            if (TextUtils.isEmpty(s.toString())) {
                posList.clear()
                postAdapter!!.notifyDataSetChanged()
            } else {
                retrievePosts(s.toString())
            }
//            timer.cancel()
//            timer = Timer()
//            timer.schedule(object : TimerTask() {
//                override fun run() {
//
//
//                }
//            }, DELAY)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun retrievePosts(text: String) {
        val postPref =
            FirebaseDatabase.getInstance().reference.child("post").orderByChild("district")
                .startAt(text.lowercase()).endAt(text.lowercase() + "\uf8ff")

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
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}