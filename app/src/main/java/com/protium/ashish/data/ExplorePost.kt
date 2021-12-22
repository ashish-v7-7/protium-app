/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExplorePost(
    var postId: String,
    var postImage: String,
    var postTag: ArrayList<String>,
    var timeStamp: Long,
    var latitude: Double,
    var longitude: Double,
    var state: String,
    var district: String,
    var publisher: String,
    var isDeleted: Boolean
) : Parcelable
