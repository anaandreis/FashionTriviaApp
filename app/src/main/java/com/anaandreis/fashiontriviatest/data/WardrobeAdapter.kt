package com.anaandreis.fashiontriviatest.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anaandreis.fashiontriviatest.R
import com.bumptech.glide.Glide
import androidx.appcompat.widget.AppCompatTextView
import com.anaandreis.fashiontriviatest.data.WardobreItem

class WardrobeLookAdapter(private val context: Context, private val wardrobeLooks: List<WardobreItem>) : RecyclerView.Adapter<WardrobeLookAdapter.ViewHolder>() {

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
    }

    override fun getItemCount(): Int {
        return wardrobeLooks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}