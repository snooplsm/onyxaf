package us.wmwm.onyx

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import us.wmwm.onyx.common.ControllerSettingName

enum class Preset(@StringRes val nameRes: Int, @DrawableRes val icon:Int, val presetData: List<PresetItem>) {
    JOHN_ANGEL(
        R.string.preset_john_angel,
        R.drawable.johnangel,
        listOf(
            PresetItem(
                ControllerSettingName.SPEED_MODE_ONE,
                value = 35
            ),
            PresetItem(
                ControllerSettingName.SPEED_MODE_TWO,
                value = 65
            ),
            PresetItem(
                ControllerSettingName.SPEED_MODE_THREE,
                value = 100
            ),
            PresetItem(
                ControllerSettingName.CURRENT_PERCENT,
                value = 75
            ),
            PresetItem(
                ControllerSettingName.BATTERY_CURRENT_LIMIT,
                value = 100
            ),
            PresetItem(
                ControllerSettingName.RLS_TPS_BRAKE_PERCENT,
                value = 0
            ),
            PresetItem(
                ControllerSettingName.ACCEL_TIME,
                value = 4
            ),
            PresetItem(
                ControllerSettingName.TORQUE_SPEED_KPS,
                value = 4000
            ),
            PresetItem(
                ControllerSettingName.TORQUE_SPEED_KI,
                value = 110
            ),
            PresetItem(
                ControllerSettingName.SPEED_ERR_LIMIT,
                value = 1100
            )
        )
    ),
    ONYX(
        R.string.preset_onyx,
        R.drawable.ic_onyx_logo,
        listOf(
            PresetItem(
                ControllerSettingName.SPEED_MODE_ONE,
                value = 35
            ),
            PresetItem(
                ControllerSettingName.SPEED_MODE_TWO,
                value = 65
            ),
            PresetItem(
                ControllerSettingName.SPEED_MODE_THREE,
                value = 1000
            ),
            PresetItem(
                ControllerSettingName.CURRENT_PERCENT, value = 50
            ),
            PresetItem(
                ControllerSettingName.BATTERY_CURRENT_LIMIT, value = 55
            ),
            PresetItem(
                ControllerSettingName.RLS_TPS_BRAKE_PERCENT, value = 50
            ),
            PresetItem(
                ControllerSettingName.ACCEL_TIME,
                value = 5
            ),
            PresetItem(
                ControllerSettingName.TORQUE_SPEED_KPS,
                value = 3000
            ),
            PresetItem(
                ControllerSettingName.TORQUE_SPEED_KI,
                value = 80
            ),
            PresetItem(
                ControllerSettingName.SPEED_ERR_LIMIT,
                value = 1000
            )
        )
    )
}