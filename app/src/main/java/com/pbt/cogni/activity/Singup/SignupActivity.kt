package com.pbt.cogni.activity.Singup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.pbt.cogni.R
import com.pbt.cogni.activity.home.MainActivity
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    var signupButton: Button? = null

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = FirebaseAuth.getInstance()

        signupButton = findViewById(R.id.btnSignUp)
        signupButton?.setOnClickListener {
            signUp()
        }


    }

    private fun signUp() {

        progress.visibility = View.VISIBLE
        signupButton?.visibility = View.GONE
        firebaseAuth?.createUserWithEmailAndPassword(
            etEmailId.text.toString(),
            etPassword.text.toString()
        )
            ?.addOnCompleteListener(this) { task ->
                Log.d("task",""+task.toString())
                if (task.isSuccessful) {
                    val user = firebaseAuth?.getCurrentUser()

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(etName.text.toString())
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT)
                                    .show();
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show();
                    progress.visibility = View.GONE
                    btnSignUp.visibility = View.VISIBLE
                }
            }

    }

}