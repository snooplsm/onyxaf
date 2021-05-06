package us.wmwm.onyx

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BikesFoundBottomSheetDialogFragmentViewModel : ViewModel() {

    val device = MutableLiveData<List<BluetoothDvc>>()

    fun init(dvc: List<BluetoothDvc>) {
        device.postValue(dvc)
    }
}