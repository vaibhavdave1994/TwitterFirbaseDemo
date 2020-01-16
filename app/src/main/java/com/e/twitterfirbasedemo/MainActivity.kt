package com.e.twitterfirbasedemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TwitterAuthProvider
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "TwitterAuth"

    private var fbAuth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                TwitterAuthConfig(
                    this.getString(R.string.twitter_Consumer_keys),
                    this.getString(R.string.twitter_Consumer_secret)
                )
            )
            .debug(true)
            .build()
        Twitter.initialize(config)
        setContentView(R.layout.activity_main)


        // Initialize Firebase Auth
        fbAuth = FirebaseAuth.getInstance()


        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

            FirebaseAuth.getInstance().signOut()

            val user = firebaseAuth.currentUser
            if (user != null) {
                user.displayName
                user.uid
                user.email
                user.phoneNumber
                if (user.photoUrl != null) {
                    user.photoUrl!!
                }
            } else {

            }

        }

//        UpdateTwitterButton()

        loginButton?.callback = object : Callback<TwitterSession>() {
            override fun success(result: com.twitter.sdk.android.core.Result<TwitterSession>?) {
                Toast.makeText(
                    this@MainActivity,
                    "Signed in to twitter successful",
                    Toast.LENGTH_LONG
                ).show()
                signInToFirebaseWithTwitterSession(result!!.data)
                loginButton?.visibility = View.VISIBLE
                //                mIndeterminateProgressBar.setVisibility(View.VISIBLE)
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }

            /* fun success(result: Result<TwitterSession>) {

                 }*/

            override fun failure(exception: TwitterException) {
                Toast.makeText(
                    this@MainActivity,
                    "Login failed. No internet or No Twitter app found on your phone",
                    Toast.LENGTH_LONG
                ).show()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //                mIndeterminateProgressBar.setVisibility(View.GONE)
//                UpdateTwitterButton()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginButton?.onActivityResult(requestCode, resultCode, data)
    }


    public override fun onStart() {
        super.onStart()
        val currentUser = fbAuth?.currentUser
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut()

        }
        authListener?.let { fbAuth?.addAuthStateListener(it) }
    }


    private fun updateUI() {
        Toast.makeText(this@MainActivity, "You're logged in", Toast.LENGTH_LONG).show()
        //Sending user to new screen after successful login
        val mainActivity = Intent(this@MainActivity, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        authListener?.let { fbAuth?.removeAuthStateListener(it) }
    }

    /* private fun UpdateTwitterButton() {
         if (TwitterCore.getInstance().sessionManager.activeSession == null) {
             loginButton?.visibility = View.VISIBLE
         } else {
             loginButton?.visibility = View.GONE
         }
     }*/


    private fun signInToFirebaseWithTwitterSession(session: TwitterSession) {
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret
        )
        fbAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(@NonNull task: Task<AuthResult>) {
                    Toast.makeText(
                        this@MainActivity,
                        "Signed in firebase twitter successful",
                        Toast.LENGTH_LONG
                    ).show()
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "Auth firebase twitter failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }


}
