package org.technoserve.cherie.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory

@Composable
fun SavedPredictionsScreen() {

    val context = LocalContext.current
    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )

    val listItems = predictionViewModel.readAllData.observeAsState(listOf()).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Predictions",
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.Black,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .background(MaterialTheme.colors.background)
        ) {
            ShowPrediction(listItems) {
                predictionViewModel.deletePrediction(it)
            }

        }
    }
}

@Composable
fun ShowPrediction(
    preditems: List<Prediction>,
    onNodeRemoved: (Prediction) -> Unit
) {
    LazyColumn {
        items(preditems) { item ->
            Row {
                PredictionCard(prediction = item)
                IconButton(onClick = { onNodeRemoved(item) }) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                }
            }

        }
    }
}

