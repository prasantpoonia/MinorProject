package com.example.mentalhealth.counsellorChat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentalhealth.adapter.CounsellorMessageAdapter
import com.example.mentalhealth.databinding.ActivityCounsellorChattingBinding
import com.example.mentalhealth.model.MessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CounsellorChattingActivity : AppCompatActivity() {
    private lateinit var messageAdapter: CounsellorMessageAdapter
    private lateinit var messageList: ArrayList<MessageData>
    private lateinit var mDbRef: DatabaseReference
    var recieverRoom: String? = null
    var senderRoom: String? = null

    private lateinit var binding: ActivityCounsellorChattingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCounsellorChattingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val email= intent.getStringExtra("email")
        val recieverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        supportActionBar?.title = email

        messageList = ArrayList()
        messageAdapter = CounsellorMessageAdapter(this, messageList)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                //@SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(MessageData::class.java)

                        messageList.add(message!!)
                    }
                    binding.chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount)
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        binding.counsellorSentButton.setOnClickListener {
            val message = binding.messageBox.text.toString()
            val messageObject = MessageData(message, senderUid)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            binding.messageBox.setText("")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home ->
            {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
