package com.example.mentalhealth.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mentalhealth.counsellorChat.CounsellorChattingActivity
import com.example.mentalhealth.R
import com.example.mentalhealth.model.Student
class CounsellorChatAdapter(val context: Context, val userList:ArrayList<Student>): RecyclerView.Adapter<CounsellorChatAdapter.CounsellorChatViewHolder>() {

    class CounsellorChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvName : TextView = itemView.findViewById(R.id.tvChatUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounsellorChatViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_layout,parent,false)
        return CounsellorChatViewHolder(view)
    }
    override fun onBindViewHolder(holder: CounsellorChatViewHolder, position: Int) {
        val currentUser=userList[position]
        holder.tvName.text=currentUser.email
        holder.itemView.setOnClickListener{
            val intent= Intent(context, CounsellorChattingActivity::class.java)
            intent.putExtra("email",currentUser.email)
            intent.putExtra("uid",currentUser.studentuid)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return userList.size
    }
}