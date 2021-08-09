package org.technoserve.cherie.ui.screens

import android.app.Application
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
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.technoserve.cherie.R
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory
import java.util.*
import kotlin.concurrent.schedule

@Composable
fun SavedPredictionsScreen() {

    val context = LocalContext.current
    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )

    val listItems = predictionViewModel.readAllData.observeAsState(listOf()).value

    val showDialog = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(true) }

    Timer().schedule(1800) {
        loading.value = false
    }

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
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        if(listItems.isNotEmpty()) showDialog.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {
        if(loading.value){
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                LinearProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading saved predictions...",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            if(listItems.isEmpty()){
                NoSavedPredictions()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(
                        items = listItems,
                        key = { item ->
                            item.id
                        }
                    ) { item ->
                        PredictionCard(prediction = item, predictionViewModel)
                    }
                }
            }
        }


        if (showDialog.value) DeleteAllDialogPresenter(showDialog, predictionViewModel)
    }
}

@Composable
fun NoSavedPredictions() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val cherryIconResource = if(isSystemInDarkTheme()) R.drawable.cherry_white else R.drawable.cherry
        Image(
            painter = painterResource(id = cherryIconResource),
            contentDescription = "",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .height(240.dp)
                .padding(top = 60.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Saved Predictions",
            color = MaterialTheme.colors.onSurface,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun DeleteAllDialogPresenter(
    showDialog: MutableState<Boolean>,
    predictionViewModel: PredictionViewModel
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(text = "Delete All Items", modifier = Modifier.height(72.dp))
        },
        text = {
            Column {
                Text("Are you sure?")
                Text("This will delete all saved predictions")
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
                        predictionViewModel.deleteAllPredictions()
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