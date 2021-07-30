package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.solver.state.helpers.AlignVerticallyReference
import androidx.lifecycle.viewmodel.compose.viewModel
import org.technoserve.cherie.R
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory

@Composable
fun SavedPredictionScreen(predictionId: Long) {

    val context = LocalContext.current as Activity
    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )

    val prediction = predictionViewModel.getSinglePrediction(predictionId).observeAsState(listOf()).value

    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Prediction",
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
            )
        }
    ) {
        if(prediction.isNotEmpty()){
            val item = prediction[0]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .wrapContentSize(Alignment.TopCenter)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    bitmap = item.inputImage.asImageBitmap(),
                    contentDescription = "Input Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(256.dp)
                        .padding(start = 32.dp, end = 32.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    bitmap = item.mask.asImageBitmap(),
                    contentDescription = "Mask",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(256.dp)
                        .padding(start = 32.dp, end = 32.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Ripeness score: ${item.ripe}",
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .requiredWidth(160.dp)
                            .background(MaterialTheme.colors.background)
                            .border(1.dp, MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(0),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "Upload",
                            modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { showDialog.value = true },
                        modifier = Modifier.requiredWidth(160.dp),
                        shape = RoundedCornerShape(0),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "Delete",
                            modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }


                if (showDialog.value) DeleteDialogPresenter(showDialog, item, predictionViewModel)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading...",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DeleteDialogPresenter(
    showDialog: MutableState<Boolean>,
    prediction: Prediction,
    predictionViewModel: PredictionViewModel
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(text = "Delete Saved Prediction", modifier = Modifier.height(72.dp))
        },
        text = {
            Column {
                Text("Are you sure?")
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = { showDialog.value = false },
                    modifier = Modifier
                        .requiredWidth(160.dp)
                        .background(MaterialTheme.colors.background)
                        .border(1.dp, MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(0),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "No, Cancel",
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        predictionViewModel.deletePrediction(prediction)
                        showDialog.value = false
                    },
                    modifier = Modifier.requiredWidth(160.dp),
                    shape = RoundedCornerShape(0),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Yes, Proceed",
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
    )
}