package us.wmwm.onyx

import android.graphics.Rect
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}
fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.expandTouchArea(dips: Int? = null) {
    val view = this
    val res = dips?.let {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            it.toFloat(),
            resources.displayMetrics
        )
    }
        ?: resources.getDimension(R.dimen.twenty)
    val resInt = res.toInt()
    post {
        val r = Rect()
        view.getHitRect(r)
        r.top -= resInt
        r.left -= resInt
        r.right += resInt
        r.bottom += resInt
        (parent as? ViewGroup)?.apply {
            touchDelegate = TouchDelegate(r, view)
        }
    }
}