package us.wmwm.onyx.db

import androidx.room.*
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.DeviceDao
import us.wmwm.onyx.domain.FlashDataReadWrite
import us.wmwm.onyx.domain.IntArrayConverter

@Database(
    entities = [BluetoothDvc::class,OnyxSettings::class, FlashDataReadWrite::class],
    version=1, exportSchema = false)
@TypeConverters(IntArrayConverter::class)
abstract class OnyxDb : RoomDatabase() {
    abstract fun device(): DeviceDao
    abstract fun settings(): SettingsDao
    abstract fun flashData(): FlashDataDao
}

