package us.wmwm.onyx

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import us.wmwm.onyx.databinding.PresetViewBinding

class PresetView(context: Context, attrs: AttributeSet?=null) : ConstraintLayout(context, attrs) {
    val b = PresetViewBinding.inflate(LayoutInflater.from(context),this, true)

    fun bind(preset: Preset) {
        b.preset.setText(preset.nameRes)
        b.icon.setImageResource(preset.icon)
    }
}