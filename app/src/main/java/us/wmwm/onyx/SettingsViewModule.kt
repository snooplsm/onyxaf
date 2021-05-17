package us.wmwm.onyx

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import us.wmwm.onyx.bluetooth.*
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.common.ControllerSettingName
import java.util.*

class SettingsViewModule(val bt: BluetoothManager) : ViewModel() {

    var data: FlashData? =
        null
//        FlashData(
//            controllerName = "KLS",
//            name = "FOO",
//            newName = "123",
//            softwareVersion2 = 2,
//            version = FlashVersion(
//                softwareVersion = SoftwareVersion.ONE,
//                identifyShowEn = true,
//                calibrationArray = emptyArray()
//            ),
//            volts = 74,
//            voltage = Voltage(60, 90),
//            currentVolt = 74,
//            date = Date(),
//            rawData = intArrayOf(0, 0, 0)
//        )

    val stack = Stack<FlashData>()

    val overrideFlashData = MutableLiveData<Consumable<FlashData>>()
    val needToReadFlashData = MutableLiveData<Consumable<Boolean>>()
    val flashData = MutableLiveData<FlashData>()
    val onReview = MutableLiveData<Consumable<List<ControllerSettingChange>>>()

    val controllerSettings = MutableLiveData<List<ControllerSettingPres>>()

    val changedSettings = mutableMapOf<ControllerSettingName, Int>()

    val reviewChanges = MutableLiveData<Consumable<Boolean>>()

    var controllerSettingsList: List<ControllerSetting> = Preset.ONYX
        .presetData.map {
            ControllerSetting(it.setting,it.value)
        }

    init {
        bt.data.observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                    data = it
                    flashData.postValue(it)
                controllerSettingsList = it.settings
                    controllerSettings.postValue(it.settings.map {
                        ControllerSettingPres(setting = it, override = it.value)
                    })
            }
        if (data == null) {
            needToReadFlashData.postValue(Consumable(true))
        } else {
            flashData.postValue(data)
        }
        bt.writeCompleted.observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                data = null
                needToReadFlashData.postValue(Consumable(true))
            }
//        controllerSettings.postValue(controllerSettingsList.map {
//            ControllerSettingPres(setting = it, override = changedSettings[it.setting] ?: it.value)
//        })
    }


    fun readFromController() {
        bt.sendCommand(Command.READ_FLASH)
    }

    fun onValueChanged(controllerSetting: ControllerSetting, newValue: Int) {
        if (controllerSettingsList.first { it.setting == controllerSetting.setting }.value != newValue) {
            changedSettings[controllerSetting.setting] = newValue
        } else {
            changedSettings.remove(controllerSetting.setting)
        }
        reviewChanges.postValue(Consumable(changedSettings.isNotEmpty()))
    }

    fun onReviewClicked() {
        val data = controllerSettingsList.filter { setting ->
            val value = changedSettings[setting.setting]
            value != null && setting.value != value
        }.map {
            ControllerSettingChange(
                from = it,
                to = it.copy(
                    value = changedSettings[it.setting]!!
                )
            )
        }
        onReview.postValue(Consumable(data))
    }

    fun onPreset(it: Preset) {
        it.presetData.forEach { presetItem ->
            if (controllerSettingsList.first { it.setting == presetItem.setting }.value != presetItem.value) {
                changedSettings[presetItem.setting] = presetItem.value
            } else {
                changedSettings.remove(presetItem.setting)
            }
        }
        controllerSettings.postValue(controllerSettingsList.map {
            ControllerSettingPres(setting = it, override = changedSettings[it.setting] ?: it.value)
        })
        reviewChanges.postValue(Consumable(changedSettings.isNotEmpty()))

    }
}