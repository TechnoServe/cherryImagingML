package org.technoserve.cherie.database

import androidx.lifecycle.LiveData

class PredictionRepository(private val predictionDAO: PredictionDAO) {

    val readAllPredictions: LiveData<List<Prediction>> = predictionDAO.getAll()

    suspend fun readPrediction(predictionId: Long) {
        predictionDAO.getPredictionById(predictionId)
    }

    suspend fun addPrediction(prediction: Prediction) {
        predictionDAO.insert(prediction)
    }

    suspend fun updatePrediction(prediction: Prediction) {
        predictionDAO.update(prediction)
    }

    suspend fun deletePrediction(prediction: Prediction) {
        predictionDAO.delete(prediction)
    }

    suspend fun deleteAllPredictions() {
        predictionDAO.deleteAll()
    }


}