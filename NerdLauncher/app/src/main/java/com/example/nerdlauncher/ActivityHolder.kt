package com.example.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    private val nameTextView: TextView = itemView.findViewById(R.id.activity_name_text_view)
    private val iconImageView: ImageView = itemView.findViewById(R.id.activity_icon)
    private lateinit var resolveInfo: ResolveInfo

    init {
        nameTextView.setOnClickListener(this)
    }

    fun bindActivity(resolveInfo: ResolveInfo) {
        this.resolveInfo = resolveInfo
        val packageManager = itemView.context.packageManager
        val appName = resolveInfo.loadLabel(packageManager).toString()
        nameTextView.text = appName

        iconImageView.setImageDrawable(resolveInfo.loadIcon(packageManager))
    }

    override fun onClick(view: View) {
        val activityInfo = resolveInfo.activityInfo
        val intent = Intent(Intent.ACTION_MAIN).apply {
            setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        view.context.startActivity(intent)
    }
}