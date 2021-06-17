package us.wmwm.onyx.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import us.wmwm.onyx.domain.FlashDataReadWrite

@Dao
interface FlashDataDao {
    @Insert
    fun save(flashDataReadWrite: FlashDataReadWrite)

    @Query("select * from FlashDataReadWrite order by created desc")
    fun list():List<FlashDataReadWrite>

    @Query("select * from FlashDataReadWrite order by created desc")
    fun listAsLiveData():LiveData<List<FlashDataReadWrite>>
}