package us.wmwm.onyx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.bluetooth.Command
import us.wmwm.onyx.bluetooth.FlashData
import us.wmwm.onyx.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    val vm: SettingsViewModule by viewModel()

    var _b:FragmentSettingsBinding?=null
    val b:FragmentSettingsBinding
    get() = _b!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _b = FragmentSettingsBinding.inflate(inflater,container,false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.needToReadFlashData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            b.recyclerView.gone()
            b.readData.visible()
        })
        vm.flashData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            b.recyclerView.visible()
            b.readData.gone()
        })
        b.readFromController.setOnClickListener {
            b.readFromController.isEnabled = false
            vm.readFromController()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

}

class SettingsViewModule(val bt:BluetoothManager) : ViewModel() {

    var data:FlashData?=null

    val stack= Stack<FlashData>()

    val overrideFlashData = MutableLiveData<Consumable<FlashData>>()
    val needToReadFlashData = MutableLiveData<Consumable<Boolean>>()
    val flashData = MutableLiveData<FlashData>()

    init {
        bt.data.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged { t1, t2 ->  t1.date==t2.date }
                .subscribe {
                    if(data==null) {
                        data = it
                        flashData.postValue(it)
                    } else {
                        stack.push(it)
                        overrideFlashData.postValue(Consumable(it))
                    }
                }
        if(data==null) {
            needToReadFlashData.postValue(Consumable(true))
        }
    }


    fun readFromController() {
        bt.sendCommand(Command.READ_FLASH)
    }
}

class Consumable<T>(private val t: T) {
    var consumed: Boolean = false

    fun consume(): T? {
        if (consumed) {
            return null
        }
        consumed = true
        return t
    }

    val peek: T = t
}