package us.wmwm.onyx.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import us.wmwm.onyx.databinding.FormSliderBinding

@SuppressLint("ResourceType")
class FormSlider(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    companion object {
        val attr = intArrayOf(
            android.R.attr.min,
            android.R.attr.max,
            android.R.attr.progress,
            android.R.attr.text
        )
        val map = attr.mapIndexed { index, i ->
            i to index
        }.toMap()
    }

    var onSettingChanged: (ControllerSetting, Int) -> Unit = { controllerSetting: ControllerSetting, i: Int -> }

    val b: FormSliderBinding = FormSliderBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        clipChildren = false
        clipToPadding = false
    }

    fun bind(option: ControllerSetting) {
        b.seek.apply {
            min = option.setting.range.first
            max = option.setting.range.last
            progress = option.value
        }
        b.value.setOnClickListener {
            b.seek.progress = option.value
            b.value.text = ""
            onSettingChanged(option,option.value)
        }
        b.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p: SeekBar, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p: SeekBar) {

            }

            override fun onStopTrackingTouch(p: SeekBar) {
                onSettingChanged(option,p.progress)
                val text = when(option.value!=p.progress) {
                    true-> "[${p.progress}]"
                    else-> ""
                }
                b.value.text = text
            }

        })
        b.label.setText(option.name)
    }
}

data class ControllerSetting(
    val setting: ControllerSettingName,
    @StringRes
    val name: Int,
    val value: Int
)

enum class ControllerSettingName(val code: Int, val range: IntRange) {
    SPEED_MODE_ONE(
        113,
        0..255
    ),
    SPEED_MODE_TWO(
        111,
        30..100
    ),
    SPEED_MODE_THREE(
        109,
        30..100
    ),
    CURRENT_PERCENT(
        code = 37,
        range = 20..100
    ),
    BATTERY_CURRENT_LIMIT(
        code = 38,
        range = 20..100
    ),
    RLS_TPS_BRAKE_PERCENT(
        code = 230,
        range = 0..50
    ),
    NTL_BRAKE_PERCENT(
        code = 231,
        range = 0..50
    ),
    ACCEL_TIME(
        code = 239,
        range = 1..25
    ),
    TORQUE_SPEED_KPS(
        code = 250,
        range = 0..32767
    )
}