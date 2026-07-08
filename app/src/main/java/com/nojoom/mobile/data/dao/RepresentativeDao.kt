package com.nojoom.mobile.data.dao

import androidx.room.*
import com.nojoom.mobile.data.entity.Representative
import kotlinx.coroutines.flow.Flow

@Dao
interface RepresentativeDao {
    @Query("SELECT * FROM representatives ORDER BY name ASC")
    fun getAll(): Flow<List<Representative>>

    @Query("SELECT * FROM representatives WHERE id = :id")
    suspend fun getById(id: Long): Representative?

    @Insert
    suspend fun insert(rep: Representative): Long

    @Update
    suspend fun update(rep: Representative)

    @Delete
    suspend fun delete(rep: Representative)
}
