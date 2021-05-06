package us.wmwm.onyx

import android.animation.ValueAnimator
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
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
        println(0xF1)
        println(-14.toByte())
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
            //b.logo.setColorFilter(ResourcesCompat.getColor(resources,R.color.online_green,theme))
            b.strips.visible()
            b.connectView.gone()
        })

        vm.disconnected.observe(this, Observer {
            b.strips.gone()
            b.connectView.visible()
            b.connected.gone()
        })
//        b.strips.visible()
//        b.connectView.gone()
        b.strips.gone()
        b.connectView.visible()
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

class MainActiivtyViewModel(val adapter: BluetoothAdapter, val manager: BluetoothManager) : ViewModel() {

    fun pausePulse() {
        manager.pausePulse()
    }

    fun resumePulse() {
        manager.resumePulse()
    }

    var conn: Disposable

    val connected = MutableLiveData<BluetoothDevice>()

    val disconnected = MutableLiveData<BluetoothDevice>()

    init {
        conn = manager.connected.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    when(it.second) {
                        BluetoothConnection.CONNECTED-> connected.postValue(it.first)
                        else-> disconnected.postValue(it.first)
                    }
                }, {

                })
    }
}