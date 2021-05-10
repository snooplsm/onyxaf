package us.wmwm.onyx.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDvc(
        @PrimaryKey
        val device: String,
        val type: Int,
        val name: String,
        val rssi: Int,
        val nickname: String?=null,
        val deleted:Long?=null,
        val date: Long = System.currentTimeMillis()
)