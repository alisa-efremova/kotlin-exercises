package com.example.nerdlauncher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setAdapter()
    }

    private fun setAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startupIntent, 0).apply {
            sortWith { a, b ->
                String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(packageManager).toString(),
                        b.loadLabel(packageManager).toString()
                )
            }
        }

        recyclerView.adapter = ActivityAdapter(activities)
    }
}