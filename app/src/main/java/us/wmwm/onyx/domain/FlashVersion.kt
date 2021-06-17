package us.wmwm.onyx.domain

import us.wmwm.onyx.domain.SoftwareVersion

data class FlashVersion(
    val softwareVersion: SoftwareVersion,
    val identifyShowEn: Boolean,
    val calibrationArray: Array<Array<String>>,
    val ids:Map<Int, Array<String>> = emptyMap()
)