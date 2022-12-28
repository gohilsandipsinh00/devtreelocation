package com.devtreelocation.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devtreelocation.model.SourceLocation

@Database(
    entities = [SourceLocation::class],
    version = 1,                // <- Database version
    exportSchema = true
)
abstract class SourceLocationDatabase :
    RoomDatabase() { // <- Add 'abstract' keyword and extends RoomDatabase

    abstract fun sourceLocationDao(): SourceLocationDao

    companion object {
        @Volatile
        private var INSTANCE: SourceLocationDatabase? = null

        fun getDatabase(context: Context): SourceLocationDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SourceLocationDatabase::class.java,
                    "source_location_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}