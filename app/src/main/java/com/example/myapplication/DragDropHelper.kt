package com.example.myapplication

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropHelper(
    private val sourceRecycler: RecyclerView,
    private val targetRecycler: RecyclerView,
    private val sourceAdapter: StringListAdapter,
    private val targetAdapter: StringListAdapter
) : ItemTouchHelper.Callback() {

    private var activeItem: String? = null
    private var fromListId: Int? = null
    private var fromPosition: Int = RecyclerView.NO_POSITION
    private var lastCenterXOnScreen: Float = -1f
    private var lastCenterYOnScreen: Float = -1f

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val adapter = recyclerView.adapter as? StringListAdapter ?: return false
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition
        if (from == RecyclerView.NO_POSITION || to == RecyclerView.NO_POSITION) return false
        adapter.moveItem(from, to)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
            val parentRecycler = viewHolder.itemView.parent as? RecyclerView
            val adapter = parentRecycler?.adapter as? StringListAdapter
            if (adapter != null) {
                fromListId = if (adapter === sourceAdapter) 0 else 1
                fromPosition = viewHolder.adapterPosition
                val list = adapter.getItems()
                activeItem = if (fromPosition in list.indices) list[fromPosition] else null
            }
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            activeItem = null
            fromListId = null
            fromPosition = RecyclerView.NO_POSITION
            lastCenterXOnScreen = -1f
            lastCenterYOnScreen = -1f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val currentAdapter = when (fromListId) {
            0 -> sourceAdapter
            1 -> targetAdapter
            else -> null
        } ?: return

        val destRecycler = resolveRecyclerUnderPoint(lastCenterXOnScreen, lastCenterYOnScreen) ?: return
        val destAdapter = destRecycler.adapter as? StringListAdapter ?: return

        if (destAdapter !== currentAdapter) {
            val item = activeItem ?: return
            val currentIndex = currentAdapter.getItems().indexOfFirst { it == item }
            val removed = if (currentIndex >= 0) currentAdapter.removeAt(currentIndex) else null
            if (removed != null) {
                val loc = IntArray(2)
                destRecycler.getLocationOnScreen(loc)
                val localX = lastCenterXOnScreen - loc[0]
                val localY = lastCenterYOnScreen - loc[1]
                val targetChild = destRecycler.findChildViewUnder(localX, localY)
                val dropPosition = if (targetChild != null) destRecycler.getChildAdapterPosition(targetChild) else destAdapter.itemCount
                val safeDrop = if (dropPosition == RecyclerView.NO_POSITION) destAdapter.itemCount else dropPosition
                destAdapter.insertAt(safeDrop, item)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            val itemView = viewHolder.itemView
            val loc = IntArray(2)
            itemView.getLocationOnScreen(loc)
            val centerX = loc[0] + itemView.width / 2f + dX
            val centerY = loc[1] + itemView.height / 2f + dY
            lastCenterXOnScreen = centerX
            lastCenterYOnScreen = centerY
        }
    }

    private fun resolveRecyclerUnderPoint(x: Float, y: Float): RecyclerView? {
        if (x < 0f || y < 0f) return null
        val out = IntArray(2)
        sourceRecycler.getLocationOnScreen(out)
        val sx = out[0].toFloat()
        val sy = out[1].toFloat()
        val sIn = x >= sx && x <= sx + sourceRecycler.width && y >= sy && y <= sy + sourceRecycler.height
        if (sIn) return sourceRecycler
        targetRecycler.getLocationOnScreen(out)
        val tx = out[0].toFloat()
        val ty = out[1].toFloat()
        val tIn = x >= tx && x <= tx + targetRecycler.width && y >= ty && y <= ty + targetRecycler.height
        return if (tIn) targetRecycler else null
    }

    override fun isLongPressDragEnabled(): Boolean = true
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }
}


