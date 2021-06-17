package us.wmwm.onyx.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.OnyxTracker
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.common.Consumable
import us.wmwm.onyx.db.OnyxDb
import us.wmwm.onyx.domain.FlashData
import us.wmwm.onyx.domain.FlashDataReadWrite
import us.wmwm.onyx.domain.ReadWrite
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class ReviewSettingsBottomSheetViewModel(val db:OnyxDb, val bt:BluetoothManager, val t: OnyxTracker): ViewModel() {

    val write = MutableLiveData<Consumable<Boolean>>()

    val data = MutableLiveData<List<ReviewPres>>()

    var changes:List<ControllerSettingChange> = emptyList()

    var dismiss = MutableLiveData<Consumable<Boolean>>()

    var flashData:FlashData? = null

    val percentage = MutableLiveData<Consumable<Int>>()

    fun init(data: FlashData, changes:List<ControllerSettingChange>) {
        this.changes = changes
        this.flashData = data
        val data = listOf(
            listOf(ReviewPres(type= ReviewType.HEADER)),
            changes.map {
                ReviewPres(
                    type= ReviewType.REVIEW,
                    data=it
                )
            },
            listOf(ReviewPres(type= ReviewType.REVIEW_SPOOF))
        ).flatten()
        this.data.postValue(data)

        bt.writeCompleted
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if(it.first) {
                    dismiss.postValue(Consumable(true))
                    val data = flashData?:throw RuntimeException("no data")
                    db.flashData()
                        .save(
                            FlashDataReadWrite(
                            serialNumber = data.serialNumber,
                            type = ReadWrite.WRITE,
                            data = it.second
                        )
                        )
                }
            }
        bt.percentage.observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                percentage.postValue(Consumable(it))
            }
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
        t.writeClicked(changes)
        bt.writeDataToKelly(changes)
    }

}