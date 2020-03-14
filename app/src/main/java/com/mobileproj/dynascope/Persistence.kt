package com.mobileproj.dynascope

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.sql.Date

@Entity
data class CounterEntity(@PrimaryKey(autoGenerate = true) val id: Int)

@Dao
interface CounterDao {
    @Query("SELECT COUNT(*) FROM CounterEntity")
    fun count(): LiveData<Int>

    @Insert
    suspend fun insert(ce: CounterEntity)
}

@Database(entities = [CounterEntity::class], version = 1)
abstract class CounterDatabase: RoomDatabase() {
    abstract fun counterDao(): CounterDao

    companion object {
        @Volatile
        private var instance: CounterDatabase? = null

        fun getDatabase(context: Context): CounterDatabase {
            val tmp = instance
            if (tmp != null) {
                return tmp
            }
            synchronized(this) {
                val tmp = Room.databaseBuilder(context.applicationContext, CounterDatabase::class.java, "database").build()
                instance = tmp
                return tmp
            }
        }
    }
}
