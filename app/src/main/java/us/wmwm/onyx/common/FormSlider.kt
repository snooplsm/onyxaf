package us.wmwm.onyx.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import us.wmwm.onyx.settings.ControllerSettingPres
import us.wmwm.onyx.databinding.FormSliderBinding
import us.wmwm.onyx.gone
import us.wmwm.onyx.visible
import java.lang.Float.max
import java.lang.Float.min
import java.text.NumberFormat

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

    var onSettingChanged: (ControllerSetting, Int) -> Unit = { controllerSetting: ControllerSetting, _: Int -> }

    val b: FormSliderBinding = FormSliderBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        clipChildren = false
        clipToPadding = false
        b.seek.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }
        b.seek.labelBehavior = LabelFormatter.LABEL_FLOATING
    }

    var listener:Slider.OnSliderTouchListener?=null

    fun bind(pres: ControllerSettingPres) {
        val option = pres.setting
        b.seek.apply {
            valueFrom = option.setting.range.first.toFloat()
            valueTo = option.setting.range.last.toFloat()
            value = option.value.toFloat()
        }
        if((option.setting.range.last-option.setting.range.first) > 300) {
            b.plus.visible()
            b.minus.visible()
        } else {
            b.plus.gone()
            b.minus.gone()
        }
        b.value.setOnClickListener {
            b.seek.value = option.value.toFloat()
            b.value.text = ""
            onSettingChanged(option,option.value)
        }
        b.plus.setOnClickListener {
            b.seek.value = min(option.setting.range.last.toFloat(),b.seek.value + b.seek.stepSize)
        }
        b.minus.setOnClickListener {
            b.seek.value = max(0f,b.seek.value - b.seek.stepSize)
        }
        listener?.let { b.seek.removeOnSliderTouchListener(it) }
        b.seek.addOnChangeListener { slider, value, fromUser ->
            onSettingChanged(option, value.toInt())
            val text = when(option.value!=slider.value.toInt()) {
                true-> "[${slider.value.toInt()}]"
                else-> ""
            }
            b.value.text = text
        }
        b.seek.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                onSettingChanged(option,slider.value.toInt())
                val text = when(option.value!=slider.value.toInt()) {
                    true-> "[${slider.value.toInt()}]"
                    else-> ""
                }
                b.value.text = text
            }
        }.apply {
            listener = this
        })
        if(pres.override!=pres.setting.value) {
            b.value.text = "[${pres.override}]"
        }
        b.label.setText(resources.getIdentifier("controller_setting_${pres.setting.setting.code}","string",context.packageName))
    }
}

data class ControllerSetting(
    val setting: ControllerSettingName,
    val value: Int
)

enum class ControllerSettingName(val code: Int, val range: IntRange, val inverse:Boolean=false) {
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
        range = 1..25,
        inverse = true
    ),
    TORQUE_SPEED_KPS(
        code = 250,
        range = 0..32767
    ),
    TORQUE_SPEED_KI(
        code = 252,
        range = 0..32767
    ),
    SPEED_ERR_LIMIT(
        code = 254,
        range = 50..4095
    )
}