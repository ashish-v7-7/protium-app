/*
 * Copyright (c) 2021.
 * Created by - Ashish Singh
 */

package com.protium.ashish.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.database.FirebaseDatabase
import com.protium.ashish.R
import java.util.concurrent.TimeUnit


class OtpVerificationActivity : AppCompatActivity() {
    var tvMobileNumber: TextView? = null

    //var tvOtpTimer: TextView? = null
    var ivWrongImage: ImageView? = null
    var btnSubmit: Button? = null
    var btnResend: Button? = null
    var tvTermsAndConditions: TextView? = null
    var tvPrivacyPolicy: TextView? = null
    lateinit var storedVerificationId: String
    var mobileNumber: String? = null
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var loadingDialog: AlertDialog? = null


    lateinit var auth: FirebaseAuth
    var maxTime = 100

    private companion object {
        private const val TAG = "OTP_VERIFICATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)



        auth = FirebaseAuth.getInstance()
        mobileNumber = intent.getStringExtra("mobileNumber").toString()


        Toast.makeText(this, mobileNumber.toString(), Toast.LENGTH_LONG).show()
        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuthReg(credential, loadingDialog!!)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                loadingDialog!!.dismiss()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                loadingDialog!!.dismiss()

            }
        }

        init()
    }

    private fun init() {
        tvMobileNumber = findViewById(R.id.activity_otp_verification_mobile_number)
        //tvOtpTimer = findViewById(R.id.activity_otp_verification_otp_timer)
        ivWrongImage = findViewById(R.id.activity_otp_verification_cross_image)
        btnSubmit = findViewById(R.id.activity_otp_verification_submit)
        btnResend = findViewById(R.id.activity_otp_verification_resend_button)
        tvTermsAndConditions = findViewById(R.id.activity_otp_verification_terms_And_conditions)
        tvPrivacyPolicy = findViewById(R.id.activity_otp_verification_privacy_policy)

//
//        val timer = object : CountDownTimer(100000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//
//                tvOtpTimer!!.text = (maxTime - 1).toString()
//            }
//
//            override fun onFinish() {
//                btnResend!!.isVisible = true
//
//            }
//        }
//        timer.start()
        tvMobileNumber!!.text = mobileNumber

        btnResend?.setOnClickListener {
            resendVerificationCode(mobileNumber!!, resendToken)
        }
        tvTermsAndConditions?.setOnClickListener {
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
        btnSubmit?.setOnClickListener {
            hideKeyboard()
            val etOtp = findViewById<EditText>(R.id.etOtp)
            var otp = etOtp.text.toString().trim()
            if (!TextUtils.isEmpty(otp)) {
                loadingDialog = showProgressDialog()
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp
                )
                firebaseAuthReg(credential, loadingDialog!!)
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        sendVerificationCode(mobileNumber!!)
    }

    private fun resendVerificationCode(phoneNumber: String, token: ForceResendingToken) {
        loadingDialog = showProgressDialog()
        val options = PhoneAuthOptions.Builder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(110L, TimeUnit.SECONDS)
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun sendVerificationCode(number: String) {
        loadingDialog = showProgressDialog()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(110L, TimeUnit.SECONDS)
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun firebaseAuthReg(credential: PhoneAuthCredential, progressDialog: AlertDialog) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val uid = user!!.uid
                    val emailID = user.email
                    val name = user.displayName
                    Log.d(TAG, "uid : $uid")
                    Log.d(TAG, "email : $emailID")

                } else {
                    Log.w(TAG, "failure", task.exception)

                }
            }
            .addOnSuccessListener { authResult ->


                val firebaseUser = auth.currentUser
                val uid = firebaseUser!!.uid

                val phone = firebaseUser.phoneNumber
                val name = firebaseUser.displayName


                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.w(TAG, "New user Created:.uid : $uid")
                    var fullname = "Unknown"
                    var cred = "hidden"
                    if (!phone.isNullOrEmpty()) {
                        cred = phone
                    }

                    if (!name.isNullOrEmpty()) {
                        fullname = name
                    }
                    saveUserInfo(fullname, cred, progressDialog)


                } else {
                    progressDialog.dismiss()
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
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
                    finishAffinity()
                    startActivity(intent)

                } else {
                    progressDialog.dismiss()
                    val err = task.exception!!.toString()
                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()


                }
            }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("GOOGLE_SIGN_IN_TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    val uid = user!!.uid
                    val emailID = user.email
                    Log.d("GOOGLE_SIGN_IN_TAG", "uid : $uid")
                    Log.d("GOOGLE_SIGN_IN_TAG", "email : $emailID")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("GOOGLE_SIGN_IN_TAG", "signInWithCredential:failure", task.exception)

                    //updateUI(null)
                }
            }
            .addOnSuccessListener { authResult ->


                val firebaseUser = auth.currentUser
                val uid = firebaseUser!!.uid
                val mobile = firebaseUser.phoneNumber

                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.w("GOOGLE_SIGN_IN_TAG", "New user Created:.uid : $uid , mobile : $mobile")

                } else {
                    Log.w("GOOGLE_SIGN_IN_TAG", "Existing User:.uid : $uid , mobile : $mobile")

                }

                //start Profile
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()


            }
            .addOnFailureListener { e ->
                Log.w("GOOGLE_SIGN_IN_TAG", "Google Login Failed:.error : ${e.message}")

            }
    }


    private fun showProgressDialog(): AlertDialog? {
        val detailBoxView =
            LayoutInflater.from(this).inflate(R.layout.custom_layout_progress_dialog, null)
        val detailsBoxBuilder = this.let { AlertDialog.Builder(it).setView(detailBoxView) }
        detailsBoxBuilder.setCancelable(false)
        return detailsBoxBuilder?.show()
    }

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