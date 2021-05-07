package us.wmwm.onyx

import androidx.room.*
import java.util.*

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

@Dao
abstract class DeviceDao {
    @Query("select * from BluetoothDvc where device=:device limit 1")
    abstract fun findByDevice(device: String): BluetoothDvc?

    @Update
    abstract fun update(device: BluetoothDvc): Int

    @Insert
    abstract fun insert(device: BluetoothDvc)

    @Transaction
    open fun insertOrUpdate(device:BluetoothDvc) {
        val d = findByDevice(device.device)
        d?.let {
            val update = d.copy(
                    rssi = device.rssi,
                    nickname = device.nickname?:d.nickname,
                    deleted = d.deleted?:device.deleted
            )
            update(update)
        }?: kotlin.run {
            insert(device)
        }



    }
}

@Database(entities = [BluetoothDvc::class], version=1)
abstract class OnyxDb : RoomDatabase() {
    abstract fun device():DeviceDao
}

