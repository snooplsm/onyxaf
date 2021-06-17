package us.wmwm.onyx.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    val top: Number = 0,
    val start: Number = 0,
    val end: Number = 0,
    val bottom: Number = 0,
    val firstStart:Number = 0,
    val lastEnd:Number = 0,
    val firstTop:Number = 0,
    val lastBottom:Number = 0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val positon = parent.getChildAdapterPosition(view)
        var firstStrt = 0
        var firstTops = 0
        var lastEd = 0
        val itemCount = state.itemCount
        var lastBottoms = 0
        if(
            positon == 0)  {
                firstStrt = firstStart.toInt()
                firstTops = firstTop.toInt()
            }
        if(positon == (itemCount-1)) {
                lastEd = lastEnd.toInt()
                lastBottoms = lastBottom.toInt()
            }

        super.getItemOffsets(outRect, view, parent, state)
        println("$itemCount $lastBottoms $lastBottom")

        outRect.set(start.toInt() + firstStrt, top.toInt()+firstTops, end.toInt()+lastEd, bottom.toInt()+lastBottoms)
    }
}