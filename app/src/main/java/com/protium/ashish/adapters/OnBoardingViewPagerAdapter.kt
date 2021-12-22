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
import androidx.viewpager.widget.PagerAdapter
import com.protium.ashish.R
import com.protium.ashish.model.OnBoardingData

class OnBoardingViewPagerAdapter(
    private var context: Context,
    private var onBoardingDataList: List<OnBoardingData>
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_on_boarding_screen, null)

        val imageView: ImageView = view.findViewById(R.id.item_on_boarding_image)
        val description: TextView = view.findViewById(R.id.item_on_boarding_description)

        imageView.setImageResource(onBoardingDataList[position].imageResource)
        description.text = onBoardingDataList[position].description
        container.addView(view)
        return view
    }
}