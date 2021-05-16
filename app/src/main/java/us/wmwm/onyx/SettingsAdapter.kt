package us.wmwm.onyx

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.common.FormSlider

class SettingsAdapter : ListAdapter<ControllerSettingPres, BaseViewHolder>(object :
    DiffUtil.ItemCallback<ControllerSettingPres>() {



    override fun areItemsTheSame(oldItem: ControllerSettingPres, newItem: ControllerSettingPres): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: ControllerSettingPres,
        newItem: ControllerSettingPres
    ): Boolean {
        return false
    }

}) {

    var onValueChanged: (ControllerSetting,Int)->Unit = { controllerSetting: ControllerSetting, i: Int -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            FormSlider(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val view = holder.view as? FormSlider ?: return
        val data = currentList[position] ?: return
        view.onSettingChanged = onValueChanged
        view.bind(data)
    }
}

data class ControllerSettingPres(
    val setting:ControllerSetting,
    val override:Int
)