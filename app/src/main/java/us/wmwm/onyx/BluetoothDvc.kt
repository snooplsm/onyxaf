package us.wmwm.onyx

import java.util.*

data class BluetoothDvc(
    val device: String,
    val type: Int,
    val name: String,
    val rssi:Int,
    val date:Date = Date()
) {


}