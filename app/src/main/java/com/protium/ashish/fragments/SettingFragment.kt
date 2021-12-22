/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.options
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.protium.ashish.R
import com.protium.ashish.activities.SignInActivity
import com.protium.ashish.data.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment() {

    private var userProfilePic: CircleImageView? = null
    private var userName: TextView? = null
    private var userAccountCred: TextView? = null
    private var updateProfilePic: TextView? = null
    private var changeLanguage: TextView? = null
    private var privacyPolicy: TextView? = null
    private var termsOfUse: TextView? = null
    private var help: TextView? = null
    private var about: TextView? = null
    private var logout: TextView? = null

    private var user: User? = null
    private var cropImage: ActivityResultLauncher<CropImageContractOptions>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        userProfilePic = view.findViewById(R.id.setting_profile_image)
        userName = view.findViewById(R.id.setting_user_full_name)
        userAccountCred = view.findViewById(R.id.setting_user_login_id)
        updateProfilePic = view.findViewById(R.id.setting_update_profile_picture)
        changeLanguage = view.findViewById(R.id.setting_change_language)
        privacyPolicy = view.findViewById(R.id.setting_privacy_policy)
        termsOfUse = view.findViewById(R.id.setting_terms_of_use)
        help = view.findViewById(R.id.setting_help)
        about = view.findViewById(R.id.setting_about_us)
        logout = view.findViewById(R.id.setting_log_out)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                val uriContent = result.uriContent
                //Picasso.get().load(uriContent).into(userProfilePic)
                updateProfilePic(uriContent)
            } else {
                val exception = result.error
            }
        }
        userName!!.setOnClickListener {
            updateNameDialog()
        }
        updateProfilePic!!.setOnClickListener {
            cropImage!!.launch(
                options {
                    setImageSource(true, true)
                        .setAspectRatio(1, 1)
                }
            )
        }

        changeLanguage!!.setOnClickListener {

            languageDialog()
        }
        privacyPolicy!!.setOnClickListener {
            showAlertDialog(
                "Privacy Policy", "Protium is founded on the conviction that engineering is " +
                        "fundamentally transforming finance in India. The digital plumbing of the economy continues to " +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "riented transformations!"
            )
        }
        termsOfUse!!.setOnClickListener {

            showAlertDialog(
                "Terms of Use", "Protium is founded on the conviction that engineering is " +
                        "fundamentally transforming finance in India. The digital plumbing of the economy continues to " +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "riented transformations!"
            )
        }
        help!!.setOnClickListener {
            showAlertDialog(
                "Help", "Contact Number : 9870881332\n Email Address : ashishsingh.v7.7@gmail.com" +
                        "\n Website : www.protium.com\n Address : Delhi, India"
            )
        }
        about!!.setOnClickListener {
            showAlertDialog(
                "Terms of Use", "Protium is founded on the conviction that engineering is " +
                        "fundamentally transforming finance in India. The digital plumbing of the economy continues to " +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "rapidly evolve - making way for even greater, exciting, customer-oriented transformations!\n\nProtium is " +
                        "founded on the conviction that engineering is fundamentally transforming finance in India. The digital " +
                        "plumbing of the economy continues to rapidly evolve - making way for even greater, exciting, customer-o" +
                        "riented transformations!"
            )
        }
        logout!!.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure ?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Yes") { dialogInterface, which ->
                Firebase.auth.signOut()
                val intent = Intent(context, SignInActivity::class.java)
                finishAffinity(requireActivity())
                startActivity(intent)

            }

            builder.setNegativeButton("Cancel") { dialogInterface, which ->

            }
            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCancelable(false)
            alertDialog.show()


        }
        getCurrentUser()

    }

    private fun languageDialog() {
        val detailBoxView =
            LayoutInflater.from(context).inflate(R.layout.custom_layout_language, null)
        val detailsBoxBuilder = context?.let { AlertDialog.Builder(it).setView(detailBoxView) }


        val CloseButton: TextView =
            detailBoxView.findViewById(R.id.custom_layout_title_des_close_button)

        val alertDialog = detailsBoxBuilder?.show()
        CloseButton.setOnClickListener {
            alertDialog?.dismiss()
        }
    }

    private fun showAlertDialog(title: String, description: String) {
        val detailBoxView =
            LayoutInflater.from(context).inflate(R.layout.custom_layout_title_description, null)
        val detailsBoxBuilder = context?.let { AlertDialog.Builder(it).setView(detailBoxView) }

        val Title: TextView = detailBoxView.findViewById(R.id.custom_layout_title_des_title)
        Title.text = title
        val Description: TextView =
            detailBoxView.findViewById(R.id.custom_layout_title_des_description)
        Description.text = description
        val CloseButton: TextView =
            detailBoxView.findViewById(R.id.custom_layout_title_des_close_button)

        val alertDialog = detailsBoxBuilder?.show()
        CloseButton.setOnClickListener {
            alertDialog?.dismiss()
        }
    }

    private fun getCurrentUser() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val userReference = FirebaseDatabase.getInstance().reference.child("user").child(uid)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value
                    val timestamp = snapshot.child("timestamp").value
                    val credential = snapshot.child("credential").value
                    val profilePic = snapshot.child("profilePic").value
                    userName!!.text = name.toString()
                    userAccountCred!!.text = credential.toString()
                    Picasso.get().load(profilePic.toString()).into(userProfilePic)
                    user = User(
                        uid,
                        name as String,
                        timestamp as Long,
                        credential as String,
                        profilePic as String
                    )

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun updateProfilePic(imageUri: Uri?) {

        val progressDialog = showProgressDialog()

        val storagePostImageReference: StorageReference? =
            FirebaseStorage.getInstance().reference.child("user_image")

        val fileRef = storagePostImageReference!!.child(user!!.uid + ".jpg")
        val uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                    //dismiss th dialog
                }
            }
            return@Continuation fileRef.downloadUrl
        })
            .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val myUrl = downloadUrl.toString()

                    val databaseReference = FirebaseDatabase.getInstance().reference.child("user")

                    val userMap = HashMap<String, Any>()
                    userMap["uid"] = user!!.uid
                    userMap["name"] = user!!.name
                    userMap["timestamp"] = System.currentTimeMillis()
                    userMap["credential"] = user!!.credential
                    userMap["profilePic"] = myUrl


                    databaseReference.child(user!!.uid).updateChildren(userMap)
                        .addOnCompleteListener { task2 ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "User update successfully.",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressDialog!!.dismiss()
                            } else {
                                progressDialog!!.dismiss()
                                val err = task2.exception!!.toString()
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()

                            }
                        }

                } else {
                    progressDialog!!.dismiss()
                    Toast.makeText(context, "Image Uploading failed", Toast.LENGTH_LONG).show()
                }

            })


    }

    private fun updateNameDialog() {
        val detailBoxView =
            LayoutInflater.from(context).inflate(R.layout.custom_layout_update_name, null)
        val detailsBoxBuilder = context?.let { AlertDialog.Builder(it).setView(detailBoxView) }

        val FullName: EditText =
            detailBoxView.findViewById(R.id.custom_layout_update_user_full_name)
        FullName.setText(user!!.name)
        val SaveButton: TextView =
            detailBoxView.findViewById(R.id.custom_layout_update_user_save_btn)

        val CloseButton: TextView =
            detailBoxView.findViewById(R.id.custom_layout_update_user_close_btn)

        val alertDialog = detailsBoxBuilder?.show()
        CloseButton.setOnClickListener {
            alertDialog?.dismiss()
        }

        SaveButton.setOnClickListener {

            hideKeyboard()
            val name = FullName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Field can't be empty", Toast.LENGTH_LONG).show()
            } else {
                val progressDialog = showProgressDialog()
                val databaseReference = FirebaseDatabase.getInstance().reference.child("user")

                val userMap = HashMap<String, Any>()
                userMap["uid"] = user!!.uid
                userMap["name"] = name
                userMap["timestamp"] = System.currentTimeMillis()
                userMap["credential"] = user!!.credential
                userMap["profilePic"] = user!!.profilePic


                databaseReference.child(user!!.uid).updateChildren(userMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "User update successfully.",
                                Toast.LENGTH_LONG
                            ).show()
                            progressDialog?.dismiss()
                            alertDialog?.dismiss()
                        } else {
                            val err = task.exception!!.toString()
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            alertDialog?.dismiss()
                        }
                    }
            }

        }
    }

    private fun showProgressDialog(): AlertDialog? {
        val detailBoxView =
            LayoutInflater.from(context).inflate(R.layout.custom_layout_progress_dialog, null)
        val detailsBoxBuilder = context?.let { AlertDialog.Builder(it).setView(detailBoxView) }
        detailsBoxBuilder!!.setCancelable(false)

        return detailsBoxBuilder.show()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }


    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}