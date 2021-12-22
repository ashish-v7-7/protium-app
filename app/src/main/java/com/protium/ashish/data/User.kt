/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.data

data class User(
    val uid: String,
    val name: String,
    val timestamp: Long,
    val credential: String,
    val profilePic: String
)
