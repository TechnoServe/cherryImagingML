package org.technoserve.cherie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PredictionDAO {

    @Query("SELECT * FROM prediction")
    fun getAll(): List<Prediction>

    @Insert
    fun insert(note: Prediction)

    @Delete
    fun delete(note: Prediction)

}