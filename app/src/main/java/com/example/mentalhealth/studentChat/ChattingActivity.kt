package com.example.mentalhealth.studentChat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mentalhealth.MessageAdapter
import com.example.mentalhealth.databinding.ActivityChattingBinding
import com.example.mentalhealth.databinding.ActivityCounsellorChattingBinding
import com.example.mentalhealth.model.MessageData
import com.example.mentalhealth.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
@ExperimentalUnsignedTypes
class ChattingActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<MessageData>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding : ActivityChattingBinding
    var recieverRoom: String?=null
    var senderRoom: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val email= intent.getStringExtra("email")
        val recieverUid= intent.getStringExtra("uid")

        val senderUid= FirebaseAuth.getInstance().currentUser?.uid
        mDbRef= FirebaseDatabase.getInstance().getReference()

        senderRoom= recieverUid+senderUid
        recieverRoom=senderUid+recieverUid

        supportActionBar?.title= email

        chatRecyclerView=binding.chatRecyclerView

        messageBox=binding.messageBox

        sentButton=binding.btnSend
        messageList= ArrayList()
        messageAdapter= MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager= LinearLayoutManager(this)
        chatRecyclerView.adapter=messageAdapter

            mDbRef.child("chats").child(senderRoom!!).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    //@SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(MessageData::class.java)
                            messageList.add(message!!)
                        }
                        chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount)
                        messageAdapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        var isAdded: Boolean=false
        mDbRef.child("student")
            .addValueEventListener(object : ValueEventListener {
                //@SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val student = postSnapshot.getValue(Student::class.java)
                        val current=FirebaseAuth.getInstance().currentUser
                        if(current!!.uid == student!!.studentuid.toString())
                            isAdded=true
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
        sentButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = MessageData(message, senderUid)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener{
                    mDbRef.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(messageObject)
                    if(!isAdded) {
                        val currentuser = FirebaseAuth.getInstance().currentUser
                        val email: String = currentuser!!.email.toString()
                        mDbRef.child("student").push()
                            .setValue(Student(email, recieverUid.toString(), senderUid.toString()))
                    }
                }
            messageBox.setText("")
        }
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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

}