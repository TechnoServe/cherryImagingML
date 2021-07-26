package org.technoserve.cherie.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(Prediction::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun predictions(): PredictionDAO

}