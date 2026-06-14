package com.superapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cached_lists")
data class CachedListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val updatedAt: Long,
)

@Entity(tableName = "cached_list_items", primaryKeys = ["id"])
data class CachedListItemEntity(
    val id: String,
    val listId: String,
    val rawName: String,
    val quantity: Double,
    val unit: String?,
)

@Dao
interface CachedListDao {
    @Query("SELECT * FROM cached_lists ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<CachedListEntity>>

    @Query("SELECT * FROM cached_list_items WHERE listId = :listId")
    suspend fun itemsOf(listId: String): List<CachedListItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertList(l: CachedListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItems(items: List<CachedListItemEntity>)

    @Query("DELETE FROM cached_list_items WHERE listId = :listId")
    suspend fun clearItems(listId: String)
}
