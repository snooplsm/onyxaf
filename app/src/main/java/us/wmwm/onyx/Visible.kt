package us.wmwm.onyx

import android.view.View

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}
fun View.hide() {
    this.visibility = View.INVISIBLE
}