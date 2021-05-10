package us.wmwm.onyx.db

import androidx.room.*

@Dao
abstract class DeviceDao {
    @Query("select * from BluetoothDvc where device=:device limit 1")
    abstract fun findByDevice(device: String): BluetoothDvc?

    @Update
    abstract fun update(device: BluetoothDvc): Int

    @Insert
    abstract fun insert(device: BluetoothDvc)

    @Transaction
    open fun insertOrUpdate(device: BluetoothDvc) {
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