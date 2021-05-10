package us.wmwm.onyx

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.common.FormSlider

class SettingsAdapter : ListAdapter<ControllerSetting, BaseViewHolder>(object :
    DiffUtil.ItemCallback<ControllerSetting>() {



    override fun areItemsTheSame(oldItem: ControllerSetting, newItem: ControllerSetting): Boolean {
        return oldItem.setting == newItem.setting
    }

    override fun areContentsTheSame(
        oldItem: ControllerSetting,
        newItem: ControllerSetting
    ): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
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