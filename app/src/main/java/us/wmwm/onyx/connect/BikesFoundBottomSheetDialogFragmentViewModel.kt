package us.wmwm.onyx.connect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.bluetooth.BluetoothConnection
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.db.BluetoothDvc

class BikesFoundBottomSheetDialogFragmentViewModel(val bt:BluetoothManager) : ViewModel() {

    val device = MutableLiveData<List<BluetoothDvc>>()

    var sub:Disposable?=null

    var dismiss = MutableLiveData<Boolean>()

    init {

    }

    fun init(dvc: List<BluetoothDvc>) {
        device.postValue(dvc)
        sub = bt.connected.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doAfterTerminate {
                dismiss = MutableLiveData()
            }
            .subscribe({
                      if(it.second==BluetoothConnection.CONNECTED) {
                          dismiss.postValue(true)
                          sub?.dispose()
                      }
            },{

            })
    }
}