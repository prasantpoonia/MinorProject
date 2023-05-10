package com.example.mentalhealth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import com.example.mentalhealth.constants.Constants
import com.example.mentalhealth.counsellorChat.CounsellorHome
import com.example.mentalhealth.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SplashActivity : AppCompatActivity() {
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        mDbRef = FirebaseDatabase.getInstance().getReference()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser!!.uid
        }
        if (currentUserId.isNotEmpty()) {
            var isCounsellor: Boolean=false
            mDbRef.child("user").addValueEventListener(object : ValueEventListener {
                //@SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val current = postSnapshot.getValue(User::class.java)
                        if (currentUserId == current?.uid) {
                            isCounsellor=true
                            Constants.hashmap[currentUserId]=true
                            val intent =
                                Intent(this@SplashActivity, CounsellorHome::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    Log.d("Reached","Here")
                    if(!isCounsellor) {
                        startActivity(Intent(this@SplashActivity, MainActivity2::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            Thread {
                try {
                    SystemClock.sleep(3500)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    startActivity(Intent(this, ChosserActivity::class.java))
                    finish()
                }
            }.start()
        }
    }
}