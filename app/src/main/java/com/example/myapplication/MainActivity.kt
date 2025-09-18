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

        leftAdapter = StringListAdapter(mutableListOf("One", "Two", "Three", "Seven", "Eight", "Nine"))
        rightAdapter = StringListAdapter(mutableListOf("Four", "Five", "Six", "Ten", "Eleven", "Twelve"))
        left.adapter = leftAdapter
        right.adapter = rightAdapter

        // Platform drag-and-drop: attach listeners to RecyclerViews
        val dragListener = StringSwapDragListener()
        left.setOnDragListener(dragListener)
        right.setOnDragListener(dragListener)

        // Initialize master order once
        OrderRegistry.ensureInitialized(listOf("one", "two", "three", "four", "five", "six"))

        // Track original (baseline) state for Cancel
        var originalLeft = leftAdapter.getItems().toList()
        var originalRight = rightAdapter.getItems().toList()

        // Cancel: restore to original baseline
        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            leftAdapter.setAll(originalLeft)
            rightAdapter.setAll(originalRight)
        }

        // Add: accept changes and log; update baseline to current
        findViewById<Button>(R.id.btn_add).setOnClickListener {
            val (leftList, rightList) = getCurrentLists()
            Log.d("Lists", "Left: $leftList | Right: $rightList")
            originalLeft = leftList
            originalRight = rightList
        }
    }

    fun getCurrentLists(): Pair<List<String>, List<String>> {
        val leftList = leftAdapter.getItems().toList()
        val rightList = rightAdapter.getItems().toList()
        return leftList to rightList
    }
}