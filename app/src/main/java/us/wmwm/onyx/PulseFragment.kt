package us.wmwm.onyx

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
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.bluetooth.Command
import us.wmwm.onyx.bluetooth.FlashData
import us.wmwm.onyx.bluetooth.MonitorData
import us.wmwm.onyx.databinding.ActivityMainBinding
import us.wmwm.onyx.databinding.FragmentPulseBinding

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

        vm.monitor.observe(viewLifecycleOwner, Observer {
            b.volts.text = it.voltage.toString()
            b.motorTemp.text = it.motorTemp.toString()
            b.controllerTemp.text = it.controllerTemp.toString()
        })
    }
}

class PulseViewModel(val bt:BluetoothManager) : ViewModel() {

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
                    monitor.postValue(it)
                }
    }
}