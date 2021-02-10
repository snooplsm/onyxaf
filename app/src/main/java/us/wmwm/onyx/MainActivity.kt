package us.wmwm.onyx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    val icons = listOf(
        ViewPage(icon = R.drawable.ic_noun_pulse_570472,
            frag = { PulseFragment() }),
        ViewPage(icon = R.drawable.ic_noun_settings_943929) { SettingsFragment() }).mapIndexed { index, viewPage ->
        index to viewPage
    }.toMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.strip)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = ViewPagerAdapter(icons, this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = ResourcesCompat.getDrawable(
                resources,
                icons[position]?.icon ?: error("Not fond"),
                null
            )
        }.attach()
    }
}

class ViewPagerAdapter(val icons: Map<Int, ViewPage>, activity: FragmentActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return icons.size
    }

    override fun createFragment(position: Int): Fragment {
        return (icons[position] ?: error("")).frag()
    }

}

class ViewPage(
    val icon: Int,
    val frag: () -> Fragment
)

class PulseFragment : Fragment()

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}