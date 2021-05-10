package us.wmwm.onyx

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.bluetooth.*
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.common.ControllerSettingName
import java.util.*

class SettingsViewModule(val bt: BluetoothManager) : ViewModel() {

    var data: FlashData? = FlashData(
        controllerName = "KLS",
        name = "FOO",
        newName = "123",
        softwareVersion2 = 2,
        version = FlashVersion(
            softwareVersion = SoftwareVersion.ONE,
            identifyShowEn = true,
            calibrationArray = emptyArray()
        ),
        volts = 74,
        voltage = Voltage(60, 90),
        currentVolt = 74,
        date = Date(),
        rawData = intArrayOf(0, 0, 0)
    )

    val stack = Stack<FlashData>()

    val overrideFlashData = MutableLiveData<Consumable<FlashData>>()
    val needToReadFlashData = MutableLiveData<Consumable<Boolean>>()
    val flashData = MutableLiveData<FlashData>()
    val onReview = MutableLiveData<Consumable<List<ControllerSettingChange>>>()

    val controllerSettings = MutableLiveData<List<ControllerSetting>>()

    val changedSettings = mutableMapOf<ControllerSettingName,Int>()

    val reviewChanges = MutableLiveData<Consumable<Boolean>>()

    val controllerSettingsList:List<ControllerSetting> = listOf(
        ControllerSetting(
            ControllerSettingName.SPEED_MODE_ONE,
            name = R.string.controller_setting_113,
            value = 200
        ),
        ControllerSetting(
            ControllerSettingName.SPEED_MODE_TWO,
            name = R.string.controller_setting_111,
            value = 70
        ),
        ControllerSetting(
            ControllerSettingName.SPEED_MODE_THREE,
            name = R.string.controller_setting_109,
            value = 30
        ),
        ControllerSetting(
            ControllerSettingName.CURRENT_PERCENT,
            name = R.string.controller_setting_37,
            value = 60
        ),
        ControllerSetting(
            ControllerSettingName.BATTERY_CURRENT_LIMIT,
            name = R.string.controller_setting_38,
            value = 75
        ),
        ControllerSetting(
            ControllerSettingName.RLS_TPS_BRAKE_PERCENT,
            name = R.string.controller_setting_230,
            value = 50
        ),
        ControllerSetting(
            ControllerSettingName.NTL_BRAKE_PERCENT,
            name = R.string.controller_setting_231,
            value = 50
        ),
        ControllerSetting(
            ControllerSettingName.ACCEL_TIME,
            name = R.string.controller_setting_239,
            value = 6
        ),
        ControllerSetting(
            ControllerSettingName.TORQUE_SPEED_KPS,
            name = R.string.controller_setting_250,
            value = 25000
        )
    )

    init {
        bt.data.observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged { t1, t2 -> t1.date == t2.date }
            .subscribe {
                if (data == null) {
                    data = it
                    flashData.postValue(it)
                } else {
                    stack.push(it)
                    overrideFlashData.postValue(Consumable(it))
                }
            }
        if (data == null) {
            needToReadFlashData.postValue(Consumable(true))
        } else {
            flashData.postValue(data)
        }
        controllerSettings.postValue(controllerSettingsList)
    }


    fun readFromController() {
        bt.sendCommand(Command.READ_FLASH)
    }

    fun onValueChanged(controllerSetting: ControllerSetting, newValue: Int) {
        if(controllerSettingsList.first { it.setting==controllerSetting.setting }.value!=newValue) {
            changedSettings[controllerSetting.setting] = newValue
        } else {
            changedSettings.remove(controllerSetting.setting)
        }
        reviewChanges.postValue(Consumable(changedSettings.isNotEmpty()))
    }

    fun onReviewClicked() {
        val data = controllerSettingsList.filter {
            val value = changedSettings[it.setting]
            value!=null && it.value!=value
        }.map {
            ControllerSettingChange(
                from=it,
                to = it.copy(
                    value = changedSettings[it.setting]!!
                )
            )
        }
        onReview.postValue(Consumable(data))
    }
}