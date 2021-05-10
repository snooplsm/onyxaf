package us.wmwm.onyx

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jaeger.library.StatusBarUtil
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.bluetooth.BluetoothConnection
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.databinding.ActivityMainBinding
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.OnyxDb


class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    val vm:MainActiivtyViewModel by viewModel()

    val icons = listOf(
            ViewPage(icon = R.raw.lottie_pulse,
                    frag = { PulseFragment() }),
            ViewPage(icon = R.raw.lottie_settings,
                    frag = { SettingsFragment() })).mapIndexed { index, viewPage ->
        index to viewPage
    }.toMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.viewPager.adapter = ViewPagerAdapter(icons, this)

        TabLayoutMediator(b.strip, b.viewPager) { tab, position ->
            val res = icons[position]?.icon ?: error("Not fond")
            val ctx = MainActivity@this
            tab.customView = LottieTabView(ctx).apply {
                bind(res)
            }
        }.attach()
        var animator:ObjectAnimator?=null
        val deanimator = ObjectAnimator.ofFloat(b.bikeImage, "translationX", -200f).apply {
            duration = 2000
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    b.bikeImage.scaleX = -1f
                }

                override fun onAnimationEnd(p0: Animator?) {
                    b.bikeImage.translationX = -800f
                    animator?.startDelay = 10000
                    animator?.start()
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }

            })
        }
        deanimator.startDelay = 5000
        animator = ObjectAnimator.ofFloat(b.bikeImage, "translationX", 1200f).apply {
            duration = 2000
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    b.bikeImage.scaleX = 1f
                    b.bikeImage.translationX = -800f
                }

                override fun onAnimationEnd(p0: Animator?) {
                    b.bikeImage.translationX = 1200f
                    deanimator.start()
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }


            })
        }

        animator.startDelay = 1000

        //animator.start()




        b.strip.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val v = tab.customView as LottieTabView
                v.play()
                when((icons[tab.position] ?: error("")).icon) {
                    R.raw.lottie_pulse-> {
                        vm.resumePulse()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val v = tab.customView as LottieTabView
                v.stop()
                when((icons[tab.position] ?: error("")).icon) {
                    R.raw.lottie_pulse-> {
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
            b.connected.text = resources.getString(R.string.connected_to_,it.name)
            //b.logo.setColorFilter(ResourcesCompat.getColor(resources,R.color.online_green,theme))
            b.strips.visible()
            b.connectView.gone()

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }
}

class UpdateListener(val lot:LottieAnimationView) : ValueAnimator.AnimatorUpdateListener {
    var count = 0.0f
    var init:Float = 0f
    override fun onAnimationUpdate(p0: ValueAnimator) {
        val diff = p0.animatedFraction-init
        count+=diff
        init = p0.animatedFraction
        if(p0.animatedFraction==1f) {
            init = 0f
        }
        if(count>=1.5f) {
            lot.pauseAnimation()
            lot.removeUpdateListener(this)
        }
    }

}


class MainActiivtyViewModel(val db: OnyxDb, val manager: BluetoothManager) : ViewModel() {

    fun pausePulse() {
        manager.pausePulse()
    }

    fun resumePulse() {
        manager.resumePulse()
    }

    var conn: Disposable

    val connected = MutableLiveData<BluetoothDvc>()

    val disconnected = MutableLiveData<BluetoothDevice>()

    init {
        conn = manager.connected.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val dvc = db.device().findByDevice(
                        it.first.address
                    )?: BluetoothDvc(
                        device = it.first.address,
                        type = it.first.type,
                        name = it.first.name,
                        rssi = Integer.MIN_VALUE
                    )
                    when(it.second) {
                        BluetoothConnection.CONNECTED-> connected.postValue(dvc)
                        else-> disconnected.postValue(it.first!!)
                    }
                }, {

                })
    }
}