package com.example.nerdlauncher

import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(private val activities: List<ResolveInfo>) :
    RecyclerView.Adapter<ActivityHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return ActivityHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        holder.bindActivity(activities[position])
    }

    override fun getItemCount(): Int {
        return activities.size
    }
}