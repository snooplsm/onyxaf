package us.wmwm.onyx.previous

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import us.wmwm.onyx.common.BaseCallback
import us.wmwm.onyx.connect.BaseViewHolder

class PreviousAdapter : ListAdapter<Previous, BaseViewHolder>(BaseCallback<Previous>()) {

    var onClicked:(Previous)->Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(PreviousView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val v = holder.view as PreviousView
        val preset = currentList[position]
        v.setOnClickListener {
            onClicked(preset)
        }
        v.bind(preset)
    }

}