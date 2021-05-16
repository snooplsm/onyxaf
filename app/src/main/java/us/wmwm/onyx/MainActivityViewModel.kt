package us.wmwm.onyx

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.bluetooth.BluetoothConnection
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.OnyxDb
import java.util.concurrent.TimeUnit

class MainActivityViewModel(val db: OnyxDb, val manager: BluetoothManager) : ViewModel() {



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
                        type = it.first.type?:0,
                        name = it.first.name?:"UNKNOWN",
                        rssi = Integer.MIN_VALUE
                    )
                    when(it.second) {
                        BluetoothConnection.CONNECTED -> connected.postValue(dvc)
                        else-> disconnected.postValue(it.first!!)
                    }
                }, {

                })
//        val dvc =  BluetoothDvc(
//            "00:11:22:33:FF:EE",
//            1,
//            "1234",
//            -30
//        )
//        manager.connect(dvc)
//        connected.postValue(
//            dvc
//        )
    }

    fun pausePulse() {
        manager.pausePulse()
        //post(true)
    }

    fun resumePulse() {
        manager.resumePulse()
        //post(false)
    }
}