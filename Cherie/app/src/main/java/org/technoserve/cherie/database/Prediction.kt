package org.technoserve.cherie.database

import android.graphics.Bitmap
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.technoserve.cherie.database.converters.BitmapConverter
import org.technoserve.cherie.database.converters.DateConverter


@Entity(tableName = "Predictions")
data class Prediction(
    @ColumnInfo(name = "inputImage", typeAffinity = ColumnInfo.BLOB)
    @TypeConverters(BitmapConverter::class)
    val inputImage: Bitmap,

    @ColumnInfo(name = "mask", typeAffinity = ColumnInfo.BLOB)
    @TypeConverters(BitmapConverter::class)
    val mask: Bitmap,

    @ColumnInfo(name = "ripe")
    val ripe: Float,

    @ColumnInfo(name = "overripe")
    val overripe: Float,

    @ColumnInfo(name = "underripe")
    val underripe: Float,

    @ColumnInfo(name = "synced")
    val synced: Boolean = false,

    @ColumnInfo(name = "scheduledForSync")
    val scheduledForSync: Boolean = false,

    @ColumnInfo(name = "createdAt")
    @TypeConverters(DateConverter::class)
    val createdAt: Long
) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Long = 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Prediction

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}