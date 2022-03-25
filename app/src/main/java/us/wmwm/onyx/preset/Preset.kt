package us.wmwm.onyx.preset

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import us.wmwm.onyx.R
import us.wmwm.onyx.common.ControllerSettingName

enum class Preset(@StringRes val nameRes: Int, @DrawableRes val icon:Int, val presetData: List<ControllerItem>) {
    ONYX(
        R.string.preset_onyx,
        R.drawable.ic_onyx_logo,
        listOf(
            ControllerItem(
                ControllerSettingName.SPEED_MODE_ONE,
                value = 35
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_TWO,
                value = 65
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_THREE,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.CURRENT_PERCENT, value = 50
            ),
            ControllerItem(
                ControllerSettingName.BATTERY_CURRENT_LIMIT, value = 55
            ),
            ControllerItem(
                ControllerSettingName.RLS_TPS_BRAKE_PERCENT, value = 1
            ),
            ControllerItem(
                ControllerSettingName.ACCEL_TIME,
                value = 5
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KPS,
                value = 3000
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KI,
                value = 80
            ),
            ControllerItem(
                ControllerSettingName.SPEED_ERR_LIMIT,
                value = 1000
            )
        )
    ),
    CHAMP(
        R.string.preset_champ,
        R.drawable.ic_champ,
        listOf(
            ControllerItem(
                ControllerSettingName.SPEED_MODE_ONE,
                value = 35
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_TWO,
                value = 65
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_THREE,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.CURRENT_PERCENT,
                value = 75
            ),
            ControllerItem(
                ControllerSettingName.BATTERY_CURRENT_LIMIT,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.RLS_TPS_BRAKE_PERCENT,
                value = 0
            ),
            ControllerItem(
                ControllerSettingName.ACCEL_TIME,
                value = 3
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KPS,
                value = 4000
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KI,
                value = 110
            ),
            ControllerItem(
                ControllerSettingName.SPEED_ERR_LIMIT,
                value = 1100
            )
        )
    ),
    TORQUE(
        R.string.preset_torque,
        R.drawable.ic_torque,
        listOf(
            ControllerItem(
                ControllerSettingName.SPEED_MODE_ONE,
                value = 35
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_TWO,
                value = 65
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_THREE,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.CURRENT_PERCENT,
                value = 75
            ),
            ControllerItem(
                ControllerSettingName.BATTERY_CURRENT_LIMIT,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.RLS_TPS_BRAKE_PERCENT,
                value = 0
            ),
            ControllerItem(
                ControllerSettingName.ACCEL_TIME,
                value = 4
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KPS,
                value = 4000
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KI,
                value = 110
            ),
            ControllerItem(
                ControllerSettingName.SPEED_ERR_LIMIT,
                value = 1100
            )
        )
    ),
    SICKO(
        R.string.preset_sicko,
        R.drawable.johnangel,
        listOf(
            ControllerItem(
                ControllerSettingName.SPEED_MODE_ONE,
                value = 35
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_TWO,
                value = 65
            ),
            ControllerItem(
                ControllerSettingName.SPEED_MODE_THREE,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.CURRENT_PERCENT,
                value = 75
            ),
            ControllerItem(
                ControllerSettingName.BATTERY_CURRENT_LIMIT,
                value = 100
            ),
            ControllerItem(
                ControllerSettingName.RLS_TPS_BRAKE_PERCENT,
                value = 0
            ),
            ControllerItem(
                ControllerSettingName.ACCEL_TIME,
                value = 1
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KPS,
                value = 4000
            ),
            ControllerItem(
                ControllerSettingName.TORQUE_SPEED_KI,
                value = 110
            ),
            ControllerItem(
                ControllerSettingName.SPEED_ERR_LIMIT,
                value = 1100
            )
        )
    ),

}