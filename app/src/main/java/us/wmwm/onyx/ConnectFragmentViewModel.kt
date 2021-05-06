package us.wmwm.onyx

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.bluetooth.BluetoothConnection
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.bluetooth.Command
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.LinkedHashSet
import kotlin.random.Random.Default.nextInt

class ConnectFragmentViewModel(
        val adapter: BluetoothAdapter,
        val manager: BluetoothManager

) : ViewModel() {

    val onDiscovering = MutableLiveData<Boolean>()

    val onDevices = MutableLiveData<List<BluetoothDvc>>()

    val needGpsOrBt = MutableLiveData<Pair<Boolean,Boolean>>()

    var sub: Disposable? = null

    var disSub: Disposable? = null

    val devices: MutableMap<String,BluetoothDvc> = mutableMapOf()

    val discarded: MutableList<BluetoothDvc> = mutableListOf()

    var canShowDevice: Boolean = true

    var autoConnectSub: Disposable? = null

    private val regex = "[0-9]{9}".toRegex()
    private val type = 7936

    init {
        manager.connected.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    println(it)
                    if(it.second==BluetoothConnection.CONNECTED) {
                        manager.sendCommand(Command.OPEN)
                    }
                }

        val random = nextInt(4,10)
        val options = listOf(-30,-35,-40,-50,-55)
        val uuids = (0..10).map { UUID.randomUUID().toString() }

        Observable.interval(5000,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {

                    (0..random)
                            .map {
                                val id = uuids[it]
                                BluetoothDvc(id,0,id,options[nextInt(0,5)])
                            }.sortedBy { it.rssi }.run {
                                onDevices.postValue(this)
                            }
                }

    }

    fun onResume() {
        connectToPreviouslyPairedDevice()
    }

    private fun connectToPreviouslyPairedDevice() {
        if (manager.isConnected) {
            return
        }
        autoConnectSub?.dispose()
        val bonded = adapter.bondedDevices
        autoConnectSub =
                io.reactivex.rxjava3.core.Observable.fromIterable(bonded)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .filter {
                            val d = it.name
                            val clazz = it.bluetoothClass.majorDeviceClass == 7936
                            regex.matches(d) && clazz
                        }
                        .doAfterNext {
                            autoConnectSub?.dispose()
                        }
                        .subscribe({
                            manager.connect(it)
                        }, {}, {})
    }

    fun discovery() {
        canShowDevice = true
        val start = adapter.startDiscovery()
        onDiscovering.postValue(start)
        disSub = Single.just(1).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .delay(12000, TimeUnit.MILLISECONDS)
                .subscribe { t1, t2 ->
                    if (adapter.isDiscovering) {
                        onDiscovering.postValue(!adapter.cancelDiscovery())
                    }
                }
    }

    fun onDevice(bluetoothDvc: BluetoothDvc) {
        devices[bluetoothDvc.device] = bluetoothDvc
        if (!canShowDevice) {
            return
        }
        sub?.dispose()
        Single.just(2).delay(10, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe { t1, t2 ->
                    canShowDevice = false
                    val list = devices.values.sortedByDescending { it.rssi }
                    onDevices.postValue(list)
                }
    }

    fun onWrongDevice(device: BluetoothDvc) {

    }

    fun onDoneDiscovering() {
        sub?.dispose()
    }

    fun onRightDevice(second: BluetoothDvc) {
        val device = adapter.getRemoteDevice(second.device)
        connect(device)
    }

    private fun connect(device: BluetoothDevice) {
        manager.connect(device)
    }

    fun onBonded(device: BluetoothDevice) {
        manager.connect(device)
    }

    fun onDisconnected(dev: BluetoothDevice) {
        manager.onDisconnect(dev)
    }

    fun onNeedGpsOrBluetooth(gpsEnabled: Boolean, bluetoothEnabled: Boolean) {
        needGpsOrBt.postValue(gpsEnabled to bluetoothEnabled)
    }

}