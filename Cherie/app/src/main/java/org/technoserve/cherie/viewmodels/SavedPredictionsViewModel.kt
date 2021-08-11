package org.technoserve.cherie.viewmodels

import org.technoserve.cherie.database.AppDatabase
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionRepository

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedPredictionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PredictionRepository
    val readAllData: LiveData<List<Prediction>>

    init {
        val predictionDAO = AppDatabase.getInstance(application).predictionsDAO()
        repository = PredictionRepository(predictionDAO)
        readAllData = repository.readAllPredictions
    }

    fun getSinglePrediction(predictionId: Long): LiveData<List<Prediction>>{
        return repository.readPrediction(predictionId)
    }

    fun addPrediction(prediction: Prediction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPrediction(prediction)
        }
    }

    fun updatePrediction(prediction: Prediction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePrediction(prediction)
        }
    }

    fun deletePrediction(prediction: Prediction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePrediction(prediction)
        }
    }

    fun deleteAllPredictions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllPredictions()
        }
    }
}

class SavedPredictionsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SavedPredictionsViewModel::class.java)) {
            return SavedPredictionsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}