package us.wmwm.onyx.settings

import us.wmwm.onyx.common.ControllerSetting

data class ControllerSettingChange(
    val from: ControllerSetting,
    val to: ControllerSetting
)