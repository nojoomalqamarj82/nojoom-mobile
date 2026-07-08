package com.nojoom.mobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nojoom.mobile.data.dao.*
import com.nojoom.mobile.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Client::class, Order::class, OrderItem::class,
        Invoice::class, Payment::class, InventoryItem::class, Representative::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun orderDao(): OrderDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun representativeDao(): RepresentativeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nojoom_mobile.db"
                ).addCallback(SeedCallback(context)).build().also { INSTANCE = it }
            }
    }
}

/** Seeds the five known regional reps on first launch. */
private class SeedCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(context).representativeDao()
            listOf(
                Representative(name = "Mohamed", region = "Comoros"),
                Representative(name = "Archad", region = "Comoros"),
                Representative(name = "Sagaf", region = "France"),
                Representative(name = "Hachim", region = "France"),
                Representative(name = "Nady", region = "Mayotte")
            ).forEach { dao.insert(it) }
        }
    }
}
