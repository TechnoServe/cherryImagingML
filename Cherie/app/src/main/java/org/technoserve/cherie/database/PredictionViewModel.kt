package org.technoserve.cherie.database


import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PredictionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "db-prediction"
    ).build()

    var predictions by mutableStateOf(listOf<Prediction>())
        private set

    // Load initial data from Room asynchronously.
    init {
        GlobalScope.launch {
            val items = db.predictions().getAll()
            viewModelScope.launch { predictions = items }
        }
    }

    fun addPrediction(prediction: String, image:ImageBitmap) {
        // Generate ID in a simple way - from timestamp.
        val predictedObj = Prediction((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), prediction, image)
        predictions = predictions + listOf(predictedObj)
        GlobalScope.launch { db.predictions().insert(predictedObj) }
    }

    fun removePrediction(prediction: Prediction) {
        predictions = predictions - listOf(prediction)
        GlobalScope.launch { db.predictions().delete(prediction) }
    }

}