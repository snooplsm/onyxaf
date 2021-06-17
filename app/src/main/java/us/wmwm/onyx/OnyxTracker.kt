package us.wmwm.onyx

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import us.wmwm.onyx.preset.Preset
import us.wmwm.onyx.settings.ControllerSettingChange

class OnyxTracker(val analytics: FirebaseAnalytics) {

    fun screen(clazz: Class<Any>) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, clazz.simpleName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, clazz.name)
        }
    }

    fun preset(it: Preset) {
        analytics.logEvent(
            "preset_selected"
        ) {
            param("preset_name", it.name)
        }
    }

    fun readFromController() {
        analytics.logEvent("read_controller") {

        }
    }

    fun reviewClicked(data: List<ControllerSettingChange>) {
        analytics.logEvent("review_clicked") {
            data.forEach {
                param(it.from.setting.name, "${it.from.value} - ${it.to.value}")
            }
        }
    }

    fun writeClicked(data: List<ControllerSettingChange>) {
        analytics.logEvent("review_clicked") {
            data.forEach {
                param(it.from.setting.name, "${it.from.value} - ${it.to.value}")
            }
        }
    }

    fun trackVenmoClicked() {
        analytics.logEvent("venmo_clicked") {

        }
    }

}
