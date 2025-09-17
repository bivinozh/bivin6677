package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemTextBinding

class StringListAdapter(
    private val items: MutableList<String>

) : RecyclerView.Adapter<StringListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.text1.text = items[position]
        // start drag-and-drop using platform API on long-press
        holder.itemView.setOnLongClickListener { v ->
            v.visibility = View.INVISIBLE
            val shadow = TextDragShadowBuilder(v)
            v.startDragAndDrop(null, shadow, v, 0)
        }
    }

    override fun getItemCount(): Int = items.size

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return
        if (fromPosition !in items.indices || toPosition !in items.indices) return
        val item = items.removeAt(fromPosition)
        items.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getItems(): MutableList<String> = items

    fun removeAt(position: Int): String? {
        if (position !in items.indices) return null
        val removed = items.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun insertAt(position: Int, value: String) {
        val safePosition = position.coerceIn(0, items.size)
        items.add(safePosition, value)
        notifyItemInserted(safePosition)
    }

    fun replaceAt(position: Int, value: String) {
        if (position !in items.indices) return
        items[position] = value
        notifyItemChanged(position)
    }
}


