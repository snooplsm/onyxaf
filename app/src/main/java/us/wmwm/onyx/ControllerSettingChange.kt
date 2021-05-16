package us.wmwm.onyx

import us.wmwm.onyx.common.ControllerSetting

data class ControllerSettingChange(
    val from: ControllerSetting,
    val to: ControllerSetting
)