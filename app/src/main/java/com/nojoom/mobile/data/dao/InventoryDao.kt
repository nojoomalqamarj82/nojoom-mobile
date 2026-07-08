package com.nojoom.mobile.data.dao

import androidx.room.*
import com.nojoom.mobile.data.entity.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY createdAt DESC")
    fun getAll(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getById(id: Long): InventoryItem?

    @Query("SELECT * FROM inventory WHERE sku LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<InventoryItem>>

    @Insert
    suspend fun insert(item: InventoryItem): Long

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)

    @Query("SELECT COUNT(*) FROM inventory")
    suspend fun count(): Int
}
