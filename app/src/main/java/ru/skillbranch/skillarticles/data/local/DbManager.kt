package ru.skillbranch.skillarticles.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.skillbranch.skillarticles.BuildConfig

object DbManager {
}

@Database(
    entities = [],
    version = AppDb.DATABASE_VERSION,
    exportSchema = false,
    views = []
)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }
}