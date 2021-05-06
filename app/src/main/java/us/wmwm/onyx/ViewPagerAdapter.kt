package us.wmwm.onyx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(val icons: Map<Int, ViewPage>, activity: FragmentActivity) :
        FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return icons.size
    }

    override fun createFragment(position: Int): Fragment {
        return (icons[position] ?: error("")).frag()
    }

}