package org.technoserve.cherie.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PredictionDAO {

    @Transaction
    @Query("SELECT * FROM Predictions")
    fun getAll(): LiveData<List<Prediction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prediction: Prediction)

    @Transaction
    @Query("SELECT * FROM Predictions WHERE id = :id ORDER BY id DESC")
    fun getPredictionById(id: Long) : LiveData<List<Prediction>>

    @Update
    suspend fun update(prediction: Prediction)

    @Delete
    suspend fun delete(prediction: Prediction)

    @Query("DELETE FROM Predictions")
    suspend fun deleteAll()

}