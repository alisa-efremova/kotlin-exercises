package com.example.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    private val nameTextView = itemView as TextView
    private lateinit var resolveInfo: ResolveInfo

    init {
        nameTextView.setOnClickListener(this)
    }

    fun bindActivity(resolveInfo: ResolveInfo) {
        this.resolveInfo = resolveInfo
        val packageManager = itemView.context.packageManager
        val appName = resolveInfo.loadLabel(packageManager).toString()
        nameTextView.text = appName
    }

    override fun onClick(view: View) {
        val activityInfo = resolveInfo.activityInfo
        val intent = Intent(Intent.ACTION_MAIN).apply {
            setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
        }
        view.context.startActivity(intent)
    }
}