package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val left = findViewById<RecyclerView>(R.id.recycler_left)
        val right = findViewById<RecyclerView>(R.id.recycler_right)

        left.layoutManager = LinearLayoutManager(this)
        right.layoutManager = LinearLayoutManager(this)

        val leftAdapter = StringListAdapter(mutableListOf("One", "Two", "Three"))
        val rightAdapter = StringListAdapter(mutableListOf("Four", "Five", "Six"))
        left.adapter = leftAdapter
        right.adapter = rightAdapter

        // Platform drag-and-drop: attach listeners to RecyclerViews
        val dragListener = StringSwapDragListener()
        left.setOnDragListener(dragListener)
        right.setOnDragListener(dragListener)
    }
}