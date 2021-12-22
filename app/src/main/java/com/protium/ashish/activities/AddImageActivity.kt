/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.options
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.protium.ashish.R
import com.protium.ashish.data.ExplorePost
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddImageActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth

    private var image: ImageView? = null
    private var imageBack: TextView? = null
    private var editImage: TextView? = null
    private var location: TextView? = null
    private var etTag: EditText? = null
    private var chipGroup: ChipGroup? = null
    private var delete: TextView? = null
    private var btnSave: Button? = null

    private var cropImage: ActivityResultLauncher<CropImageContractOptions>? = null
    private var chipText = ArrayList<String>()
    private var imageUri: Uri? = null
    private var storagePostImageReference: StorageReference? = null
    private var myUrl = ""


    private var explorePost: ExplorePost? = null
    private var postId: String? = null
    private var isUpdate: Boolean? = null
    private var isImageUpdate: Boolean = false
    private var isChipCreate: Boolean = true

    //map variables
    var PERMISSION_CODE = 205
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var country: String? = null
    private var subCountry: String? = null
    private var state: String? = null
    private var district: String? = null
    private var loadingLocation: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        init()
        isUpdate = intent.getBooleanExtra("isUpdatePost", false)
        Log.e("1", "1")
        if (isUpdate as Boolean) {
            postId = intent.getStringExtra("postId").toString()
            //postPosition= intent.getIntExtra("postPosition",-1)
            getPost(postId!!)


        }

    }

    private fun setData(ExplorePost: ExplorePost?) {
        Log.e("1", "2")
        delete!!.isVisible = false
        btnSave!!.text = "Update Post"
        Log.e("1", "2")
        if (isChipCreate) {
            for (chipText in ExplorePost!!.postTag) {
                creteMyChips(chipText)

            }
        }
        location!!.text =
            ExplorePost!!.district + ", " + ExplorePost.state + " (lat: " + ExplorePost.latitude + ", long: " + ExplorePost.longitude + ")"
        Log.e("1", "2")
        Picasso.get().load(ExplorePost.postImage).into(image)
        Log.e("1", "2")
    }

    private fun getPost(postId: String) {
        Log.e("1", "2")
        val postReference = FirebaseDatabase.getInstance().reference.child("post").child(postId)
        Log.e("1", "2")
        postReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val postID = snapshot.child("postId").value
                    val postImage = snapshot.child("postImage").value
                    val postTag = snapshot.child("postTag").value
                    val timeStamp = snapshot.child("timeStamp").value
                    val latitude = snapshot.child("latitude").value
                    val longitude = snapshot.child("longitude").value
                    val state = snapshot.child("state").value
                    val district = snapshot.child("district").value
                    val publisher = snapshot.child("publisher").value
                    val isDeleted = snapshot.child("isDeleted").value
                    Log.e("1", "2")
                    val PostID = postID as String
                    val PostImage = postImage as String
                    val PostTag = postTag as ArrayList<String>
                    val TimeStamp = timeStamp as Long
                    val Latitude = latitude as Double
                    val Longitude = longitude as Double
                    val State = state as String
                    val District = district as String
                    val Publisher = publisher as String
                    val IsDeleted = isDeleted as Boolean
                    Log.e("1", "2")

                    explorePost = ExplorePost(
                        PostID,
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
                    Log.e("1", "2")
                    setData(explorePost!!)
                    Log.e("1", "2")

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun init() {

        image = findViewById(R.id.activity_add_image_image)
        imageBack = findViewById(R.id.activity_add_image_back)
        editImage = findViewById(R.id.activity_add_image_edit)
        location = findViewById(R.id.activity_add_image_location)
        etTag = findViewById(R.id.activity_add_image_edit_text_tag)
        chipGroup = findViewById(R.id.activity_add_image_chip_group)
        delete = findViewById(R.id.activity_add_image_delete_post)
        btnSave = findViewById(R.id.activity_add_image_save_post)

        image!!.setOnClickListener {
            cropImage!!.launch(
                options {
                    setImageSource(true, true)
                    //.setAspectRatio(4,3)
                }
            )
        }
        editImage!!.setOnClickListener {
            cropImage!!.launch(
                options {
                    setImageSource(true, true)
                    //  .setAspectRatio(4,3)
                }
            )
        }
        imageBack!!.setOnClickListener {
            finish()
        }
        btnSave!!.setOnClickListener {

            if (isUpdate as Boolean) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirm Update")
                builder.setMessage("Are you sure ?")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                builder.setPositiveButton("Yes") { dialogInterface, which ->
                    saveImage()
                }

                builder.setNegativeButton("Cancel") { dialogInterface, which ->

                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            } else {
                saveImage()
            }

        }
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uriContent = result.uriContent
                Picasso.get().load(uriContent).into(image)
                imageUri = uriContent
                isImageUpdate = true
            } else {
                val exception = result.error
            }
        }
        entryChips()
    }

    private fun creteMyChips(txt: String) {
        val chip = Chip(this)
        chip.apply {
            text = txt
            chipIcon = ContextCompat.getDrawable(
                this@AddImageActivity,
                R.drawable.ic_launcher_foreground
            )
            isChipIconVisible = false
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            chipGroup!!.addView(chip as View)
            setOnCloseIconClickListener {
                chipGroup!!.removeView(chip as View)
            }
        }
    }

    private fun entryChips() {
        etTag!!.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP
            ) {

                val name = etTag!!.text.toString()
                creteMyChips(name.substring(0, name.length - 1))
                etTag!!.text.clear()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun getAllChip(): List<String> {
        val myList = ArrayList<String>()
        val selectedChips = chipGroup!!.children
            .map { (it as Chip).text.toString() }
        for (chipText in selectedChips) {
            myList.add(chipText)
        }
        return myList
    }

    private fun saveImage() {

        hideKeyboard()
        val currentTimeStamp: Long = System.currentTimeMillis()
        if (!TextUtils.isEmpty(etTag!!.text.toString())) {
            chipText.add(etTag!!.text.toString().lowercase())
        }
        chipText.addAll(getAllChip())

        if (isImageUpdate) {
            if (imageUri == null) {
                Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            if (!isUpdate!!) {
                if (imageUri == null) {
                    Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
                    return
                }
            }

        }
        if (TextUtils.isEmpty(state)) {
            onGPS()
        }
        when {

            chipText.size == 0 -> Toast.makeText(this, "Please Add Some Tag", Toast.LENGTH_SHORT)
                .show()

            else -> {

                val alertDialog = showProgressDialog("Please Wait...")

                if (isUpdate as Boolean) {
                    if (isImageUpdate) {
                        val fileRef =
                            storagePostImageReference!!.child(explorePost!!.timeStamp.toString() + ".jpg")
                        val uploadTask: StorageTask<*>
                        uploadTask = fileRef.putFile(imageUri!!)

                        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                    //dismiss th dialog
                                    alertDialog!!.dismiss()
                                }
                            }
                            return@Continuation fileRef.downloadUrl
                        })
                            .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                                if (task.isSuccessful) {
                                    val downloadUrl = task.result
                                    myUrl = downloadUrl.toString()
                                    val databaseReference =
                                        FirebaseDatabase.getInstance().reference.child("post")
                                    val postId = explorePost!!.postId

                                    val postMap = HashMap<String, Any>()
                                    postMap["postId"] = postId
                                    postMap["postImage"] = myUrl
                                    postMap["postTag"] = chipText
                                    postMap["timeStamp"] = explorePost!!.timeStamp
                                    postMap["latitude"] = explorePost!!.latitude
                                    postMap["longitude"] = explorePost!!.longitude
                                    postMap["state"] = explorePost!!.state
                                    postMap["district"] = explorePost!!.district
                                    postMap["publisher"] = explorePost!!.publisher
                                    postMap["isDeleted"] = false

                                    isChipCreate = false
                                    databaseReference.child(postId).updateChildren(postMap)
                                        .addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Post has been updated successfully.",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                alertDialog!!.dismiss()
                                                finish()
                                            } else {
                                                val err = task2.exception!!.toString()
                                                Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                                                alertDialog!!.dismiss()


                                            }
                                        }

                                } else {
                                    Toast.makeText(this, "Post updated failed", Toast.LENGTH_LONG)
                                        .show()
                                    alertDialog!!.dismiss()
                                }

                            })

                    } else {
                        val databaseReference =
                            FirebaseDatabase.getInstance().reference.child("post")
                        val postId = explorePost!!.postId

                        val postMap = HashMap<String, Any>()
                        postMap["postId"] = explorePost!!.postId
                        postMap["postImage"] = explorePost!!.postImage
                        postMap["postTag"] = chipText
                        postMap["timeStamp"] = explorePost!!.timeStamp
                        postMap["latitude"] = explorePost!!.latitude
                        postMap["longitude"] = explorePost!!.longitude
                        postMap["state"] = explorePost!!.state
                        postMap["district"] = explorePost!!.district
                        postMap["publisher"] = explorePost!!.publisher
                        postMap["isDeleted"] = false
                        isChipCreate = false

                        databaseReference.child(postId).updateChildren(postMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Post has been updated successfully.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    alertDialog!!.dismiss()
                                    finish()
                                } else {
                                    val err = task.exception!!.toString()
                                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                                    alertDialog!!.dismiss()


                                }
                            }

                    }

                } else {
                    val fileRef =
                        storagePostImageReference!!.child(currentTimeStamp.toString() + ".jpg")
                    val uploadTask: StorageTask<*>
                    uploadTask = fileRef.putFile(imageUri!!)

                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                                //dismiss th dialog
                                alertDialog!!.dismiss()
                            }
                        }
                        return@Continuation fileRef.downloadUrl
                    })
                        .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                            if (task.isSuccessful) {
                                val downloadUrl = task.result
                                myUrl = downloadUrl.toString()
                                val databaseReference =
                                    FirebaseDatabase.getInstance().reference.child("post")
                                val postId = databaseReference.push().key

                                val postMap = HashMap<String, Any>()
                                postMap["postId"] = postId!!
                                postMap["postImage"] = myUrl
                                postMap["postTag"] = chipText
                                postMap["timeStamp"] = currentTimeStamp
                                postMap["latitude"] = latitude as Double
                                postMap["longitude"] = longitude as Double
                                postMap["state"] = state.toString().lowercase()
                                postMap["district"] = district.toString().lowercase()
                                postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                                postMap["isDeleted"] = false

                                databaseReference.child(postId).updateChildren(postMap)
                                    .addOnCompleteListener { task2 ->
                                        if (task2.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Post has been created successfully.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            alertDialog!!.dismiss()
                                            finish()
                                        } else {
                                            val err = task2.exception!!.toString()
                                            Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                                            alertDialog!!.dismiss()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Post created failed", Toast.LENGTH_LONG)
                                    .show()
                                alertDialog!!.dismiss()
                            }
                        })
                }
            }
        }
    }

    private fun showProgressDialog(message: String): AlertDialog? {
        val detailBoxView =
            LayoutInflater.from(this).inflate(R.layout.custom_layout_progress_dialog, null)
        val detailsBoxBuilder = this.let { AlertDialog.Builder(it).setView(detailBoxView) }
        val progressText: TextView = detailBoxView.findViewById(R.id.progress_dialog_text)
        progressText.text = message
        detailsBoxBuilder.setCancelable(false)

        return detailsBoxBuilder?.show()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        } else {
            firebaseUser = firebaseAuth.currentUser!!
            storagePostImageReference = FirebaseStorage.getInstance().reference.child("post_image")
            if (TextUtils.isEmpty(state)) {
                onGPS()
            }

        }
    }

    //map implementation
    // get location


    private fun onGPS() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                if (checkPermission()) {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            Log.e("loc", "REQUEST NEW LOCATION")
                            getNewLocation()
                        } else {

                            latitude = location.latitude
                            longitude = location.longitude
                            getCountryName(latitude!!, longitude!!)
                        }
                    }
                } else {
                    requestPermission()
                }
            } else {
                requestLocation()
            }
        } else {
            requestPermission()
        }
    }

    // get location

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        loadingLocation = showProgressDialog("Fetching Location...")
        locationRequest = LocationRequest.create()

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()!!
        )


    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location = p0.lastLocation
            latitude = lastLocation.latitude
            longitude = lastLocation.longitude

            loadingLocation!!.dismiss()
            getCountryName(latitude!!, longitude!!)

        }
    }

    private fun getCountryName(lat: Double, long: Double) {

        val geoCoder = Geocoder(this, Locale.getDefault())
        val Adress = geoCoder.getFromLocation(lat, long, 1)

        Log.e("city", Adress[0].toString())
        country = Adress[0].countryName
        subCountry = Adress[0].locality
        state = Adress[0].adminArea
        district = Adress[0].subAdminArea


        location!!.text = district + ", " + state + " (lat: " + lat + ", long: " + long + ")"
    }

    private fun requestLocation() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    //check the user permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false
    }

    //function to get user permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //check the permission result
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            Log.e("loc", "REQUEST NEW LOCATION")
                            getNewLocation()
                        } else {

                            latitude = location.latitude
                            longitude = location.longitude
                            getCountryName(latitude!!, longitude!!)
                        }
                    }
                } else {
                    requestLocation()
                }

            } else {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission")
        builder.setMessage("Do you want to continue ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, which ->
            onGPS()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, which ->
            finish()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        latitude = null
        longitude = null
        state = null
        district = null
    }
}