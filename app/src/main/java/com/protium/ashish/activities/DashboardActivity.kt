/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.protium.ashish.R
import com.protium.ashish.fragments.ExploreFragment
import com.protium.ashish.fragments.HomeFragment
import com.protium.ashish.fragments.SearchFragment
import com.protium.ashish.fragments.SettingFragment

class DashboardActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    private val home = 1
    private val explore = 2
    private val search = 3
    private val setting = 4
    private var selected = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val navView: BottomNavigationView = findViewById(R.id.dashboard_bottom_nav)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard_bottom_nav_home -> {

                    gotoFragment(HomeFragment(), home)
                    return@setOnItemSelectedListener true

                }
                R.id.dashboard_bottom_nav_explore -> {
                    gotoFragment(ExploreFragment(), explore)
                    return@setOnItemSelectedListener true

                }
                R.id.dashboard_bottom_nav_search -> {
                    gotoFragment(SearchFragment(), search)
                    return@setOnItemSelectedListener true

                }
                R.id.dashboard_bottom_nav_setting -> {
                    gotoFragment(SettingFragment(), setting)
                    return@setOnItemSelectedListener true

                }
            }
            false
        }
        gotoFragment(HomeFragment(), home)
    }


    private fun gotoFragment(fragment: Fragment, fNo: Int) {
        if (selected != fNo) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.dashboard_frame_layout, fragment)
            fragmentTransaction.commit()
            selected = fNo
        }


    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {

        if (selected != home) {
            gotoFragment(HomeFragment(), home)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Message")
            builder.setMessage("Are you sure want to exit ?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                super.onBackPressed()
            }
            builder.setNegativeButton("Cancel") { dialogInterface, which ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }


    }
}





