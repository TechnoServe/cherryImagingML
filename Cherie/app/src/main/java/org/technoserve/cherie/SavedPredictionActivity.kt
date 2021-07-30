package org.technoserve.cherie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.technoserve.cherie.ui.screens.SavedPredictionScreen
import org.technoserve.cherie.ui.theme.CherieTheme

class SavedPredictionActivity : ComponentActivity() {
    private val predictionId by lazy {
        intent.getLongExtra(ID, 0L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CherieTheme {
                SavedPredictionScreen(predictionId)
            }
        }
    }

    companion object {
        const val ID = "predictionId"

        fun newIntent(context: Context, predictionId: Long) =
            Intent(context, SavedPredictionActivity::class.java).apply {
                putExtra(ID, predictionId)
            }
    }
}
