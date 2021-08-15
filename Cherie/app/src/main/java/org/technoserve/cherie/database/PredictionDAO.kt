package org.technoserve.cherie.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PredictionDAO {

    @Transaction
    @Query("SELECT * FROM Predictions ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<Prediction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prediction: Prediction)

    @Transaction
    @Query("SELECT * FROM Predictions WHERE id = :id ORDER BY id DESC")
    fun getPredictionById(id: Long) : LiveData<List<Prediction>>

    @Transaction
    @Query("SELECT * FROM Predictions WHERE id = :id ORDER BY id DESC")
    fun getRawPredictionById(id: Long) : List<Prediction>

    @Update
    suspend fun update(prediction: Prediction)

    @Delete
    suspend fun delete(prediction: Prediction)

    @Query("DELETE FROM Predictions")
    suspend fun deleteAll()

    @Query("UPDATE Predictions SET synced=1 WHERE id = :id")
    suspend fun updateSyncStatus(id: Long)

    @Query("UPDATE Predictions SET scheduledForSync=1 WHERE id IN (:ids)")
    suspend fun updateSyncListStatus(ids: List<Long>)

    @Query("DELETE FROM Predictions WHERE id IN (:ids)")
    suspend fun deleteList(ids: List<Long>)

}