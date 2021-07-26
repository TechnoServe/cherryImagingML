package org.technoserve.cherie.database

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Prediction (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "image") val image: ImageBitmap
)