/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.protium.ashish.R
import com.protium.ashish.data.ExplorePost
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private var explorePostList: MutableList<ExplorePost>? = null
    private var isComingFromExplore: Boolean? = false

    var userName = "Current Location"

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
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        isComingFromExplore = intent.getBooleanExtra("isComingFromExplore", false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        explorePostList = ArrayList()
        retrievePosts()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
    }

    private fun retrievePosts() {

        if (isComingFromExplore == true) {
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


                        val postLoc = LatLng(Latitude, Longitude)

                        //Zoom level - 1: World, 5: Landmass/continent, 10: City, 15: Streets and 20: Buildings
                        mMap.addMarker(
                            MarkerOptions().position(postLoc).icon(
                                BitmapDescriptorFactory
                                    .fromBitmap(createDrawableFromView(PostImage))
                            )
                        )
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(postLoc, 10f))


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
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        } else {
            val postPref =
                FirebaseDatabase.getInstance().reference.child("post").orderByChild("publisher")
                    .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
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


                        val postLoc = LatLng(Latitude, Longitude)

                        //Zoom level - 1: World, 5: Landmass/continent, 10: City, 15: Streets and 20: Buildings
                        mMap.addMarker(
                            MarkerOptions().position(postLoc).icon(
                                BitmapDescriptorFactory
                                    .fromBitmap(createDrawableFromView(PostImage))
                            )
                        )
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(postLoc, 10f))


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
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


    }

    private fun centreMapOnLocation(lat: Double, long: Double, title: String) {
        val postLoc = LatLng(lat, long)
        mMap.addMarker(MarkerOptions().position(postLoc).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(postLoc))
    }

    private fun createDrawableFromView(imgUri: String): Bitmap {
        val customMarkerView: View = View.inflate(
            this,
            R.layout.custom_location_marker, null
        )

        val imgView =
            customMarkerView.findViewById<ImageView>(R.id.custom_location_marker_imageView)
        Picasso.get().load(imgUri).into(imgView)

        imgView.setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_LONG).show()
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )

        val bitmap =
            Bitmap.createBitmap(
                customMarkerView.width,
                customMarkerView.height,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        customMarkerView.draw(canvas)
        return bitmap
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast(this)
        return true
    }

    override fun onStart() {
        super.onStart()
        if (TextUtils.isEmpty(state)) {
            onGPS()
        }
    }

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
            getCountryName(latitude!!, longitude!!)

            loadingLocation!!.dismiss()

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


        val userReference = FirebaseDatabase.getInstance().reference.child("user")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.child("name").value
                    userName = user as String
                    centreMapOnLocation(lat, long, userName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        centreMapOnLocation(lat, long, userName)
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
                onGPS()
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
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
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
}