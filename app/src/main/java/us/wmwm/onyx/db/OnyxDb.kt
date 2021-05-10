package us.wmwm.onyx.db

import androidx.room.*
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.DeviceDao

@Database(entities = [BluetoothDvc::class,OnyxSettings::class], version=1, exportSchema = false)
abstract class OnyxDb : RoomDatabase() {
    abstract fun device(): DeviceDao
    abstract fun settings(): SettingsDao
}

