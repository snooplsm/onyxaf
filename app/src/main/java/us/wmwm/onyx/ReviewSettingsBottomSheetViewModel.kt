package us.wmwm.onyx

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.bluetooth.BluetoothManager
import java.util.concurrent.TimeUnit

class ReviewSettingsBottomSheetViewModel(val bt:BluetoothManager): ViewModel() {

    val write = MutableLiveData<Consumable<Boolean>>()

    val data = MutableLiveData<List<ReviewPres>>()

    var changes:List<ControllerSettingChange> = emptyList()

    fun init(changes:List<ControllerSettingChange>) {
        this.changes = changes
        val data = listOf(
            listOf(ReviewPres(type=ReviewType.HEADER)),
            changes.map {
                ReviewPres(
                    type=ReviewType.REVIEW,
                    data=it
                )
            },
            listOf(ReviewPres(type=ReviewType.REVIEW_SPOOF))
        ).flatten()
        this.data.postValue(data)
    }

    fun onViewCreated() {
        Single.just(1)
            .delay(1, TimeUnit.SECONDS)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe { t1, t2 ->
                write.postValue(Consumable(true))
            }
    }

    fun onWriteClicked() {
        bt.writeDataToKelly(changes)
    }

}