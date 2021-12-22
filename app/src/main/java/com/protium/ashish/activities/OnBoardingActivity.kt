/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.protium.ashish.R
import com.protium.ashish.adapters.OnBoardingViewPagerAdapter
import com.protium.ashish.model.OnBoardingData

class OnBoardingActivity : AppCompatActivity() {
    var onBoardingPageAdapter: OnBoardingViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var onBoardingViewPager: ViewPager? = null
    var nextButton: Button? = null
    var position = 0
    var sharedPreferences: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (restorePrefData()) {
            val i = Intent(applicationContext, SignInActivity::class.java)
            startActivity(i)
            finish()
        }

        setContentView(R.layout.activity_on_boarding)

        tabLayout = findViewById(R.id.on_boarding_activity_tab_indicator)
        nextButton = findViewById(R.id.on_boarding_activity_button)

        // add some data to model class
        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(
            OnBoardingData(
                "Protium Finance",
                "Protium Finance offers secured and unsecured loans to MSMEs anywhere in India.\n Protium Finance\",\"Protium Finance offers secured and unsecured loans to MSMEs anywhere in India. ",
                R.drawable.protium_finance_logo
            )
        )
        onBoardingData.add(
            OnBoardingData(
                "Protium Money",
                "Protium Money is our consumer lending platform with a huge network of trusted partners offering effortless credit solutions. So more freedom for you, when you need it most.",
                R.drawable.protium_money_logo
            )
        )
        onBoardingData.add(
            OnBoardingData(
                "Protium Sakshara",
                "Protium Sakshara offers customized loan products for edupreneurs and educational institutions to help fund your capital or operational expenditure requirements.",
                R.drawable.protium_sakshara_logo
            )
        )

        setOnViewPagerAdapter(onBoardingData)
        position = onBoardingViewPager!!.currentItem
        nextButton?.setOnClickListener {
            if (position < onBoardingData.size) {
                position++
                onBoardingViewPager!!.currentItem = position
            }

            if (position == onBoardingData.size) {
                savePrefData()
                val i = Intent(applicationContext, SignInActivity::class.java)
                startActivity(i)
                finish()
            }
        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1) {
                    nextButton!!.text = "Get Started"
                } else {
                    nextButton!!.text = "Next"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setOnViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPager = findViewById(R.id.on_boarding_activity_view_pager)
        onBoardingPageAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingPageAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }

    private fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("isFirstTimeRun", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", false)
    }
}