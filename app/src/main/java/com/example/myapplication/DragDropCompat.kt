package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Point
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TextDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        super.onProvideShadowMetrics(size, touch)
        // center touch point for a natural feel
        val w = view.width
        val h = view.height
        touch.set(w / 2, h / 2)
    }
    override fun onDrawShadow(canvas: Canvas) {
        super.onDrawShadow(canvas)
    }
}

class StringSwapDragListener : View.OnDragListener {
    private var draggedPosition: Int = RecyclerView.NO_POSITION
    private var draggedRecyclerView: RecyclerView? = null
    private var draggedData: String? = null
    private var hoverRecyclerView: RecyclerView? = null
    private var hoverIndex: Int = RecyclerView.NO_POSITION
    private var highlightedChild: View? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onDrag(view: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                val sourceView = event.localState as? View ?: return false
                draggedRecyclerView = sourceView.parent as? RecyclerView
                draggedPosition = draggedRecyclerView?.getChildAdapterPosition(sourceView)
                    ?: RecyclerView.NO_POSITION
                if (draggedPosition == RecyclerView.NO_POSITION) return false
                val srcAdapter = draggedRecyclerView?.adapter as? StringListAdapter ?: return false
                val data = srcAdapter.getItems()
                if (draggedPosition !in data.indices) return false
                draggedData = data[draggedPosition]
                hoverRecyclerView = null
                hoverIndex = RecyclerView.NO_POSITION
                highlightedChild = null
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                // Track current item under finger and visually mark it as focused
                val rv = (if (view is RecyclerView) view else view.parent as? RecyclerView)
                if (rv != null) {
                    val child = if (view is RecyclerView) view.findChildViewUnder(event.x, event.y) else view
                    val idx = if (child != null) rv.getChildAdapterPosition(child) else RecyclerView.NO_POSITION
                    if (rv !== hoverRecyclerView || idx != hoverIndex) {
                        // clear previous highlight
                        highlightedChild?.alpha = 1f
                        highlightedChild = null
                        hoverRecyclerView = rv
                        hoverIndex = idx
                        if (idx != RecyclerView.NO_POSITION && child != null) {
                            child.alpha = 0.7f
                            highlightedChild = child
                        }
                    }
                }
                return true
            }
            DragEvent.ACTION_DROP -> {
                view.post {
                    val dragged = draggedData ?: return@post
                    val sourceRv = draggedRecyclerView ?: return@post
                    val srcAdapter = sourceRv.adapter as? StringListAdapter ?: return@post
                    val srcIdx = srcAdapter.getItems().indexOfFirst { it == dragged }
                    if (srcIdx == -1) return@post

                    val targetRv = (if (view is RecyclerView) view else view.parent as? RecyclerView)
                        ?: return@post
                    val tgtAdapter = targetRv.adapter as? StringListAdapter ?: return@post

                    // Prefer focused (hovered) index if available
                    val targetIndex = if (hoverRecyclerView === targetRv && hoverIndex != RecyclerView.NO_POSITION) {
                        hoverIndex
                    } else if (view is RecyclerView) {
                        val child = view.findChildViewUnder(event.x, event.y)
                        val pos = if (child != null) view.getChildAdapterPosition(child) else RecyclerView.NO_POSITION
                        if (pos != RecyclerView.NO_POSITION) pos else (tgtAdapter.itemCount - 1).coerceAtLeast(0)
                    } else {
                        val pos = targetRv.getChildAdapterPosition(view)
                        if (pos != RecyclerView.NO_POSITION) pos else (tgtAdapter.itemCount - 1).coerceAtLeast(0)
                    }

                    if (targetRv === sourceRv) {
                        // Reorder within the same list
                        val to = targetIndex.coerceIn(0, srcAdapter.itemCount - 1)
                        if (srcIdx != to) srcAdapter.moveItem(srcIdx, to)
                    } else {
                        // Cross-list with fixed sizes: remove from source, insert into target at drop index,
                        // then shift the target's first item back to the end of the source to keep counts fixed.
                        val srcInitialSize = srcAdapter.itemCount
                        val tgtInitialSize = tgtAdapter.itemCount

                        val removed = srcAdapter.removeAt(srcIdx) ?: return@post
                        val insertPos = targetIndex.coerceIn(0, tgtAdapter.itemCount)
                        tgtAdapter.insertAt(insertPos, removed)

                        if (tgtAdapter.itemCount > tgtInitialSize) {
                            val shifted = tgtAdapter.removeAt(0)
                            if (shifted != null) {
                                // append to end of source
                                srcAdapter.insertAt(srcAdapter.itemCount, shifted)
                            }
                        }
                    }
                }
                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                (event.localState as? View)?.visibility = View.VISIBLE
                highlightedChild?.alpha = 1f
                highlightedChild = null
                hoverRecyclerView = null
                hoverIndex = RecyclerView.NO_POSITION
                draggedPosition = RecyclerView.NO_POSITION
                draggedRecyclerView = null
                draggedData = null
                return true
            }
        }
        return true
    }
}


