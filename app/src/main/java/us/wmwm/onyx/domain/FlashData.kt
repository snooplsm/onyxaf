package us.wmwm.onyx.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.json.JSONArray
import us.wmwm.onyx.common.ControllerSetting
import java.util.*

data class FlashData(
    val controllerName: String,
    val serialNumber: String,
    val name: String,
    val newName: String,
    val softwareVersion2: Int,
    val version: FlashVersion,
    val volts: Int,
    val voltage: Voltage?,
    val currentVolt:Int,
    val date: Date = Date(),
    val settings:List<ControllerSetting>,
    val rawData:IntArray
)

@Entity
data class FlashDataReadWrite(

   @PrimaryKey(autoGenerate = true)
   val id:Int = 0,
   val serialNumber:String,
   val type:ReadWrite,
   val created:Long = System.currentTimeMillis(),
   val data:IntArray

)

enum class ReadWrite {
    READ,WRITE
}

class IntArrayConverter {

    @TypeConverter
    fun fromString(str:String):IntArray {
        val arr = JSONArray(str)
        return (0..arr.length()).map { arr.getInt(it) }.toIntArray()
    }

    @TypeConverter
    fun toString(arr:IntArray):String {
        return JSONArray().run {
            arr.forEach { this.put(it) }
            this.toString()
        }
    }

}