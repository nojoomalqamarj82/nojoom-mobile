package com.nojoom.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val country: String = "",       // UAE, France, Comoros, Mayotte...
    val city: String = "",
    val repName: String = "",       // Mohamed, Archad, Sagaf, Hachim, Nady
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
