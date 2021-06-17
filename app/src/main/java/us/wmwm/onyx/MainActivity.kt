package us.wmwm.onyx

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jaeger.library.StatusBarUtil
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.common.LottieTabView
import us.wmwm.onyx.connect.ConnectedBottomSheetDialogFragment
import us.wmwm.onyx.connect.ConnectedFragmentViewModel
import us.wmwm.onyx.connect.MotorStayBottomSheetDialogFragment
import us.wmwm.onyx.databinding.ActivityMainBinding
import us.wmwm.onyx.settings.BikeProgrammedBottomSheetDialogFragment
import us.wmwm.onyx.settings.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    val vm: MainActivityViewModel by viewModel()
    val connectedVm: ConnectedFragmentViewModel by viewModel()

    val t:OnyxTracker by inject()

    val icons = listOf(
        ViewPage(icon = R.raw.lottie_pulse,
            frag = { PulseFragment() }),
        ViewPage(icon = R.raw.lottie_settings,
            frag = { SettingsFragment() })
    ).mapIndexed { index, viewPage ->
        index to viewPage
    }.toMap()

    val listener = PageListenerForReize()

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.viewPager.adapter = ViewPagerAdapter(icons, this)

        TabLayoutMediator(b.strip, b.viewPager, true, true) { tab, position ->
            val res = icons[position]?.icon ?: error("Not fond")
            val ctx = MainActivity@ this
            tab.customView = LottieTabView(ctx).apply {
                bind(res)
            }
        }.attach()
        b.motorStay.setOnClickListener {
            MotorStayBottomSheetDialogFragment().apply {
                t.screen(this.javaClass)
            }.show(supportFragmentManager,"motor-stay")
        }
        b.motorStay.expandTouchArea()
        b.viewPager.registerOnPageChangeCallback(listener)
        b.strip.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val v = tab.customView as LottieTabView
                v.play()
                when ((icons[tab.position] ?: error("")).icon) {
                    R.raw.lottie_pulse -> {
                        vm.resumePulse()
                        listener.pageAfterAction = {
                            val slide = Slide(Gravity.START)
                            //slide.interpolator = OvershootInterpolator()
                            TransitionManager.beginDelayedTransition(b.logo.parent as ViewGroup,slide)
                            b.logoSmall.gone()
                            b.logo.visible()
                            b.spacer.visible()
                        }
                    }
                    R.raw.lottie_settings -> {

                        listener.pageAfterAction = {
                            val slide = Slide(Gravity.START)
                            val set = TransitionSet()
                            slide.addTarget(b.logoSmall)
                            set.addTransition(slide)
                            val slideUp = Slide(Gravity.START)
                            slideUp.addTarget(b.logo)
                            set.addTransition(slideUp)
                            TransitionManager.beginDelayedTransition(b.logo.parent as ViewGroup,set)
                            b.logo.gone()
                            b.logoSmall.visible()
                            b.spacer.gone()
                        }
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val v = tab.customView as LottieTabView
                v.stop()
                when ((icons[tab.position] ?: error("")).icon) {
                    R.raw.lottie_pulse -> {

                        vm.pausePulse()
                    }
                }
                tab.position
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })

        vm.connected.observe(this, Observer {
            b.connected.visible()
            b.connected.text = resources.getString(R.string.connected_to_, it.name)
            b.strips.visible()
            b.connectView.gone()
            b.connected.setOnClickListener {v->
                connectedVm.init(it)
                ConnectedBottomSheetDialogFragment().apply {
                    t.screen(this.javaClass)
                }.show(supportFragmentManager,"connected")
            }
        })

        vm.disconnected.observe(this, Observer {
            b.strips.hide()
            b.connectView.visible()
            b.connected.gone()
        })

//        b.strips.visible()
//        b.connectView.gone()
        b.strips.hide()

        b.connectView.visible()

        //BikeProgrammedBottomSheetDialogFragment().show(supportFragmentManager,"bike")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }
}

class PageListenerForReize : ViewPager2.OnPageChangeCallback() {

    var pageAfterAction: () -> Unit = { }

    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
        println("statechanged $state")
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        println("onPageScrolled $position $positionOffset")
        when (positionOffset) {
            1.0f, 0f -> {
                pageAfterAction.invoke()
                pageAfterAction = {}
            }
        }
    }

    override fun onPageSelected(position: Int) {
        println("selected $position")
        super.onPageSelected(position)
    }
}


