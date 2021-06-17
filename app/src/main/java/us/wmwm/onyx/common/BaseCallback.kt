package us.wmwm.onyx.common

import androidx.recyclerview.widget.DiffUtil

class BaseCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode()==newItem.hashCode()
    }

}