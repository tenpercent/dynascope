package com.mobileproj.dynascope

import androidx.room.*
import java.util.*

@Entity
data class CounterEntity(@PrimaryKey val id: Int,
                   @ColumnInfo(name = "date") val date: Date,
                   @ColumnInfo(name = "counter") val counter: Int)

@Dao
interface CounterDao {
    @Query("SELECT * FROM CounterEntity WHERE date = :date LIMIT 1")
    fun findByDate(date: Date): CounterEntity

    @Insert
    fun insertAll(vararg counters: CounterEntity)

    @Delete
    fun delete(counter: CounterEntity)
}

@Database(entities = [CounterEntity::class], version = 1)
abstract class CounterDatabase: RoomDatabase() {
    abstract fun counterDao(): CounterDao
}
