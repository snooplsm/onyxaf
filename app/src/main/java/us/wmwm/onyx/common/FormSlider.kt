package us.wmwm.onyx.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import us.wmwm.onyx.databinding.FormSliderBinding

@SuppressLint("ResourceType")
class FormSlider(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    companion object {
        val attr = intArrayOf(android.R.attr.max, android.R.attr.progress,android.R.attr.text)
    }

    val b: FormSliderBinding = FormSliderBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        clipChildren = false
        clipToPadding = false
        val a = context.obtainStyledAttributes(attrs, attr)
        val t = a.getText(2)
        val m = a.getInt(a.getIndex(0),1)
        val d = a.getInt(a.getIndex(1), 1)
        b.label.text = t
        b.seek.apply {
            max = m
            progress = d
        }
        a.recycle()
    }
}