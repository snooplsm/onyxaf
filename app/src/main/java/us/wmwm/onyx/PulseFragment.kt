package us.wmwm.onyx

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.bluetooth.Command
import us.wmwm.onyx.bluetooth.FlashData
import us.wmwm.onyx.bluetooth.MonitorData
import us.wmwm.onyx.databinding.ActivityMainBinding
import us.wmwm.onyx.databinding.FragmentPulseBinding
import us.wmwm.onyx.db.OnyxDb
import us.wmwm.onyx.db.Temp

class PulseFragment : Fragment() {

    val bluetoothManager:BluetoothManager by inject()

    val vm:PulseViewModel by viewModel()

    private var _b: FragmentPulseBinding?=null

    val b:FragmentPulseBinding
    get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentPulseBinding.inflate(inflater,container,false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.read.setOnClickListener {
            bluetoothManager.sendCommand(Command.READ_FLASH)
        }
        b.read.gone()
        b.celsius.isSelected = true
        b.volts.isSelected = true
        b.volt.isSelected = true

        b.fahrenheit.setOnClickListener {
            vm.onTemp(Temp.FAHRENHEIT)
        }

        b.celsius.setOnClickListener {
            vm.onTemp(Temp.CELSIUS)
        }

        vm.monitor.observe(viewLifecycleOwner, Observer {
            b.volts.text = it.voltage.toString()
            b.motorTemp.text = it.motorTemp.toString()
            b.controllerTemp.text = it.controllerTemp.toString()
        })

        vm.temps.observe(viewLifecycleOwner, Observer {
            var toSelect:TextView
            var toUnselect:TextView
            when(it) {
                Temp.CELSIUS-> {
                    toSelect = b.celsius
                    toUnselect = b.fahrenheit
                }
                Temp.FAHRENHEIT-> {
                    toSelect = b.fahrenheit
                    toUnselect = b.celsius
                }
            }
            toSelect.isSelected = true
            toSelect.setTypeface(null, Typeface.BOLD)
            toUnselect.isSelected = false
            toUnselect.setTypeface(null, Typeface.NORMAL)
        })
    }
}

class PulseViewModel(val bt:BluetoothManager, val db:OnyxDb) : ViewModel() {

    val temps = MutableLiveData<Temp>()

    init {
        Single.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { t1, t2 ->
                temps.postValue(db.settings().settings().temp)
            }
    }

    fun onTemp(temp: Temp) {
        Single.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { t1, t2 ->
                db.settings().settings().copy(
                    temp = temp
                ).run {
                    db.settings().update(this)
                    temps.postValue(this.temp)

                }
            }
    }

    val data = MutableLiveData<FlashData>()
    val monitor = MutableLiveData<MonitorData>()

    init {
        bt.data.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    data.postValue(it)
                }
        bt.monitor.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    val settings = db.settings().settings()
                    monitor.postValue(it.copy(
                        motorTemp = settings.temp(it.motorTemp),
                        controllerTemp = settings.temp(it.controllerTemp)
                    ))
                }
    }
}