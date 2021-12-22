/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.protium.ashish.R
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {

    //mobile
    lateinit var auth: FirebaseAuth

    private var mobileNumber: String? = null

    //google sign in
    lateinit var googleSignInClient: GoogleSignInClient

    var loadingDialog: AlertDialog? = null

    private companion object {
        private const val RC_SIGN_IN = 101
        private const val TAG = "SIGN_ACTIVITY"
    }

    //facebook sign in
    var callbackManager: CallbackManager? = null

    lateinit var resultLauncher: ActivityResultLauncher<Intent>


    var etMobileNumber: EditText? = null
    var continueWithMobileButton: Button? = null
    var continueWithGoogleButton: Button? = null
    var continueWithFacebookButton: Button? = null
    var tvTermsAndCondition: TextView? = null
    var tvPrivacyPolicy: TextView? = null

    val REG = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}\$"
    var PATTERN: Pattern = Pattern.compile(REG)
    fun CharSequence.isPhoneNumber(): Boolean = PATTERN.matcher(this).find()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        init()

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            finishAffinity()
            finish()
        }

        // Callback function for Phone Auth


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RC_SIGN_IN) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                        firebaseAuthReg(credential)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
            }

        //configure facebook sign in

        callbackManager = CallbackManager.Factory.create()
        val loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton.setPermissions("email")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }

            override fun onSuccess(result: LoginResult) {
                //handleFacebookAccessToken(result.accessToken.token)
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                firebaseAuthReg(credential)
            }

        })
    }


    private fun init() {
        etMobileNumber = findViewById(R.id.activity_sign_in_mobile_number)
        continueWithMobileButton = findViewById(R.id.activity_sign_in_continue_with_mobile_number)
        continueWithGoogleButton = findViewById(R.id.activity_sign_in_continue_with_google)
        continueWithFacebookButton = findViewById(R.id.activity_sign_in_continue_with_facebook)
        tvTermsAndCondition = findViewById(R.id.activity_sign_in_terms_And_conditions)
        tvPrivacyPolicy = findViewById(R.id.activity_sign_in_privacy_policy)

        continueWithMobileButton?.setOnClickListener {
            hideKeyboard()
            var number = etMobileNumber?.text.toString().trim()

            Log.e("valid", number.isPhoneNumber().toString())
            if (number.isPhoneNumber()) {
                number = "+91" + number
                mobileNumber = number
                val intent = Intent(applicationContext, OtpVerificationActivity::class.java)
                intent.putExtra("mobileNumber", mobileNumber)
                startActivity(intent)
            } else {
                etMobileNumber!!.error = "Invalid Mobile Number"
                etMobileNumber!!.requestFocus()
                Toast.makeText(this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
            }
        }

        continueWithGoogleButton?.setOnClickListener {

            val signInIntent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)

        }
        continueWithFacebookButton?.setOnClickListener {

        }
        tvTermsAndCondition?.setOnClickListener {
            showLegalDialog(
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
        tvPrivacyPolicy?.setOnClickListener {
            showLegalDialog(
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
                        "riented transformations!"
            )
        }
    }

    private fun firebaseAuthReg(credential: AuthCredential) {
        val progressDialog = showProgressDialog()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                } else {
                    Log.w(TAG, "failure", task.exception)

                    //updateUI(null)
                }
            }
            .addOnSuccessListener { authResult ->


                val firebaseUser = auth.currentUser
                val uid = firebaseUser!!.uid
                val emailId = firebaseUser.email
                val phone = firebaseUser.phoneNumber
                val name = firebaseUser.displayName


                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.w(TAG, "New user Created:.uid : $uid , email : $emailId")
                    var fullname = "Unknown"
                    var cred = "hidden"
                    if (!emailId.isNullOrEmpty()) {
                        cred = emailId
                    } else if (!phone.isNullOrEmpty()) {
                        cred = phone
                    }

                    if (!name.isNullOrEmpty()) {
                        fullname = name
                    }
                    saveUserInfo(fullname, cred, progressDialog!!)


                } else {
                    progressDialog!!.dismiss()
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    finishAffinity()
                    startActivity(intent)

                }


            }
            .addOnFailureListener { e ->
                progressDialog!!.dismiss()
                Log.w(TAG, "Google Login Failed:.error : ${e.message}")

            }
    }


    private fun saveUserInfo(fullName: String, credential: String, progressDialog: AlertDialog) {


        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        //create base node reference with "user"
        val userReference = FirebaseDatabase.getInstance().reference.child("user")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["name"] = fullName
        userMap["timestamp"] = System.currentTimeMillis()
        userMap["credential"] = credential
        userMap["profilePic"] =
            "https://firebasestorage.googleapis.com/v0/b/protium-d8c09.appspot.com/o/default_image%2Fdefault_pic.png?alt=media&token=cd0e5889-e29f-4fcb-9d9f-873cd3b49f2b"

        userReference.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Account has been created successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                    //start Profile
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    progressDialog.dismiss()
                    val err = task.exception!!.toString()
                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()


                }
            }
    }

    private fun showProgressDialog(): AlertDialog? {
        val detailBoxView =
            LayoutInflater.from(this).inflate(R.layout.custom_layout_progress_dialog, null)
        val detailsBoxBuilder = this.let { AlertDialog.Builder(it).setView(detailBoxView) }
        detailsBoxBuilder.setCancelable(false)
        return detailsBoxBuilder?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        // callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onActivityReenter(resultCode: Int, data: Intent?) {
//        super.onActivityReenter(resultCode, data)
//        callbackManager?.onActivityReenter( resultCode, data)
//    }

    private fun showLegalDialog(title: String, description: String) {
        val detailBoxView =
            LayoutInflater.from(this).inflate(R.layout.custom_layout_title_description, null)
        val detailsBoxBuilder = this.let { AlertDialog.Builder(it).setView(detailBoxView) }

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


    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}