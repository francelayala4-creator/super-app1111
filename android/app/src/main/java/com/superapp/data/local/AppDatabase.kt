package com.superapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.superapp.data.local.dao.CachedListDao
import com.superapp.data.local.dao.CachedListEntity
import com.superapp.data.local.dao.CachedListItemEntity

@Database(entities = [CachedListEntity::class, CachedListItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedListDao(): CachedListDao
}
