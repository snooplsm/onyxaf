package us.wmwm.onyx.db

import android.database.sqlite.SQLiteDatabase
import androidx.room.*

@Dao
abstract class SettingsDao {

    @Query("select * from OnyxSettings limit 1")
    abstract fun find(): OnyxSettings?

    @Transaction
    open fun settings(): OnyxSettings {
        return find() ?: OnyxSettings().apply {
            save(this)
        }
    }

    @Insert(onConflict = SQLiteDatabase.CONFLICT_REPLACE)
    abstract fun save(settings: OnyxSettings)

    @Update
    abstract fun update(settings: OnyxSettings)
}