package com.nojoom.mobile.data

import androidx.room.TypeConverter
import com.nojoom.mobile.data.entity.InvoiceStatus
import com.nojoom.mobile.data.entity.MaterialType
import com.nojoom.mobile.data.entity.OrderStatus

class Converters {
    @TypeConverter
    fun fromMaterialType(v: MaterialType): String = v.name
    @TypeConverter
    fun toMaterialType(v: String): MaterialType = MaterialType.valueOf(v)

    @TypeConverter
    fun fromOrderStatus(v: OrderStatus): String = v.name
    @TypeConverter
    fun toOrderStatus(v: String): OrderStatus = OrderStatus.valueOf(v)

    @TypeConverter
    fun fromInvoiceStatus(v: InvoiceStatus?): String? = v?.name
    @TypeConverter
    fun toInvoiceStatus(v: String?): InvoiceStatus? = v?.let { InvoiceStatus.valueOf(it) }
}
