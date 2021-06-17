package us.wmwm.onyx.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OnyxSettings(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val temp: Temp = Temp.FAHRENHEIT
) {
    fun temp(temperature: Int): Int {
        return when(temp) {
            Temp.CELSIUS-> temperature
            Temp.FAHRENHEIT -> (temperature.times(9/5)+32)
        }
    }
}