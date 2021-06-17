package us.wmwm.onyx.preset

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import us.wmwm.onyx.common.BaseCallback
import us.wmwm.onyx.connect.BaseViewHolder

class PresetAdapter : ListAdapter<Preset, BaseViewHolder>(BaseCallback<Preset>()) {

    var onClicked:(Preset)->Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(PresetView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val v = holder.view as PresetView
        val preset = currentList[position]
        v.setOnClickListener {
            onClicked(preset)
        }
        v.bind(preset)
    }

}