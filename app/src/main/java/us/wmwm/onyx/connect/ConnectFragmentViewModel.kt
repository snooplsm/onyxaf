package us.wmwm.onyx.connect

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.common.Consumable
import us.wmwm.onyx.bluetooth.BluetoothConnection
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.bluetooth.Command
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.OnyxDb
import java.util.concurrent.TimeUnit

class ConnectFragmentViewModel(
    val adapter: BluetoothAdapter,
    val manager: BluetoothManager,
    val db: OnyxDb,
) : ViewModel() {

    val onDiscovering = MutableLiveData<Boolean>()

    val countdown = MutableLiveData<Int>()

    val onDevices = MutableLiveData<List<BluetoothDvc>>()

    val needGpsOrBt = MutableLiveData<Consumable<Pair<Boolean, Boolean>>>()

    var sub: Disposable? = null

    var disSub: Disposable? = null

    val devices: MutableMap<String, BluetoothDvc> = mutableMapOf()

    val discarded: MutableList<BluetoothDvc> = mutableListOf()

    var canShowDevice: Boolean = true

    var autoConnectSub: Disposable? = null

    private val regex = "[0-9]{8,9}".toRegex()
    private val type = 7936

    init {
        manager.connected.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    if(it.second==BluetoothConnection.CONNECTED) {
                        manager.sendCommand(Command.OPEN)
                    }
                }

//        val random = nextInt(4,10)
//        val options = listOf(-30,-35,-40,-50,-55)
//        val uuids = (0..10).map { UUID.randomUUID().toString() }.toMutableList()
//        uuids[0] = "DAO"
//
//        Observable.interval(5000,TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe {
//                    (0..random)
//                            .map {
//                                val id = uuids[it]
//                                BluetoothDvc(id,0,id,options[nextInt(0,5)])
//                            }.map {
//                                db.device().findByDevice(it.device)?:it
//                        }
//                            .sortedBy { it.rssi }.run {
//                                onDevices.postValue(this)
//                            }
//                }

    }

    fun onResume() {
        //connectToPreviouslyPairedDevice()
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

        val total = 12
        disSub = Observable.intervalRange(0,total.toLong(),0,1,TimeUnit.SECONDS)
            .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
            .subscribe( {
                countdown.postValue(total-(it.toInt()+1))
            },  {

            },  {
                countdown.postValue(0)
                if (adapter.isDiscovering) {
                    onDiscovering.postValue(!adapter.cancelDiscovery())
                }
            })
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
            .map {
                devices.values.map {
                    db.device().findByDevice(it.device)?:it
                }.sortedWith(
                    compareBy(
                        {it.nickname!=null},{it.rssi},
                    )
                )
            }
                .subscribe { t1, t2 ->
                    canShowDevice = false

                    onDevices.postValue(t1)
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
        needGpsOrBt.postValue(Consumable(gpsEnabled to bluetoothEnabled))
    }

}