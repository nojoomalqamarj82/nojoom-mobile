package com.nojoom.mobile.data.dao

import androidx.room.*
import com.nojoom.mobile.data.entity.Order
import com.nojoom.mobile.data.entity.OrderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getById(id: Long): Order?

    @Query("SELECT * FROM orders WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getByClient(clientId: Long): Flow<List<Order>>

    @Insert
    suspend fun insert(order: Order): Long

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun count(): Int

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsForOrder(orderId: Long): Flow<List<OrderItem>>

    @Insert
    suspend fun insertItem(item: OrderItem): Long

    @Update
    suspend fun updateItem(item: OrderItem)

    @Delete
    suspend fun deleteItem(item: OrderItem)
}
