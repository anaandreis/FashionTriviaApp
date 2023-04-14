package com.anaandreis.fashiontriviatest.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anaandreis.fashiontriviatest.R
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class WardrobeLookAdapter(private val context: Context, private val wardrobeLooks: MutableList<WardobreItem>) : RecyclerView.Adapter<WardrobeLookAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wardrobeLook = wardrobeLooks[position]

        (holder.itemView.findViewById(R.id.wardrobeText) as TextView).text = wardrobeLook.description

        Glide.with(context)
            .load(wardrobeLook.imageUrl)
            .into(holder.itemView.findViewById(R.id.imageView) as ImageView)

        val removeButton = holder.itemView.findViewById(R.id.buttonRemove) as Button
        removeButton.setOnClickListener {
            // Call the deleteWardrobeLookFromFirebase method with the document ID of the selected wardrobe look
            deleteWardrobeLookFromFirebase(wardrobeLook.id)
            val index = wardrobeLooks.indexOf(wardrobeLook)
            if (index != -1) {
                wardrobeLooks.removeAt(index)
                notifyItemRemoved(index)
            }
        }

    }

    override fun getItemCount(): Int {
        return wardrobeLooks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

fun deleteWardrobeLookFromFirebase(documentId: String?) {
    val database = FirebaseDatabase.getInstance()
    val wardrobeLooksRef = database.getReference("wardrobelooks")

    if (documentId != null) {
        wardrobeLooksRef.child(documentId).removeValue()
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Wardrobe look successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error deleting wardrobe look", e)
            }
    }
}