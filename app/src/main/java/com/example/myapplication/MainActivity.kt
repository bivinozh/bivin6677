package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var leftAdapter: StringListAdapter
    private lateinit var rightAdapter: StringListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val left = findViewById<RecyclerView>(R.id.recycler_left)
        val right = findViewById<RecyclerView>(R.id.recycler_right)

        left.layoutManager = LinearLayoutManager(this)
        right.layoutManager = LinearLayoutManager(this)

        leftAdapter = StringListAdapter(mutableListOf("One", "Two", "Three"))
        rightAdapter = StringListAdapter(mutableListOf("Four", "Five", "Six"))
        left.adapter = leftAdapter
        right.adapter = rightAdapter

        // Platform drag-and-drop: attach listeners to RecyclerViews
        val dragListener = StringSwapDragListener()
        left.setOnDragListener(dragListener)
        right.setOnDragListener(dragListener)

        // Button to log current lists
        findViewById<Button>(R.id.btn_log_lists).setOnClickListener {
            val (leftList, rightList) = getCurrentLists()
            Log.d("Lists", "Left: $leftList | Right: $rightList")
        }
    }

    fun getCurrentLists(): Pair<List<String>, List<String>> {
        val leftList = leftAdapter.getItems().toList()
        val rightList = rightAdapter.getItems().toList()
        return leftList to rightList
    }
}