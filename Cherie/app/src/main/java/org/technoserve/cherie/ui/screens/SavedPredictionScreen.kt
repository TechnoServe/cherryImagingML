package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.storage.FirebaseStorage
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory
import org.technoserve.cherie.helpers.ImageUtils
import org.technoserve.cherie.ui.components.ButtonPrimary
import org.technoserve.cherie.ui.components.ButtonSecondary


@Composable
fun SavedPredictionScreen(predictionId: Long) {

    val context = LocalContext.current as Activity
    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )

    val prediction =
        predictionViewModel.getSinglePrediction(predictionId).observeAsState(listOf()).value

    val showDialog = remember { mutableStateOf(false) }

    fun upload(item: Prediction) {
        val fileName = item.id + item.createdAt
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        val combinedBitmaps = ImageUtils.combineBitmaps(item.inputImage, item.mask)
        val imageUri = ImageUtils.createTempBitmapUri(context, combinedBitmaps)
        Log.d("ImageURI", imageUri.toString())
        //TODO: Update value in RoomDB to synced
        storageReference.putFile(imageUri).addOnSuccessListener {
            Log.d("UPLOAD", "Uploaded successfully" + it.uploadSessionUri.toString())
        }.addOnFailureListener {
            Log.d("UPLOAD", "Upload Failed")
        }
    }

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
                    IconButton(onClick = {
                        val returnIntent = Intent()
                        context.setResult(Activity.RESULT_CANCELED, returnIntent)
                        context.finish()
                    }) {
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
        if (prediction.isNotEmpty()) {
            val item = prediction[0]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .wrapContentSize(Alignment.TopCenter)
                    .padding(top = 32.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    bitmap = item.mask.asImageBitmap(),
                    contentDescription = "Mask",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(256.dp)
                        .padding(start = 32.dp, end = 32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ripeness score: ${item.ripe}",
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ButtonSecondary(onClick = { upload(item) }, label = "Upload") {
                        Row(
                            modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CloudUpload, "", tint = MaterialTheme.colors.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Upload",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    ButtonPrimary(onClick = { showDialog.value = true }, label = "Delete") {
                        Row(
                            modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Delete, "", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Delete",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }


                if (showDialog.value) DeleteDialogPresenter(showDialog, item, predictionViewModel)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                LinearProgressIndicator()
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
    val context = LocalContext.current as Activity
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
            ) {
                ButtonSecondary(onClick = { showDialog.value = false }, label = "No, Cancel")
                Spacer(modifier = Modifier.width(16.dp))
                ButtonPrimary(onClick = {
                    val returnIntent = Intent()
                    returnIntent.putExtra("ID", prediction.id)
                    predictionViewModel.deletePrediction(prediction)
                    showDialog.value = false
                    context.setResult(DELETED, returnIntent)
                    context.finish()
                }, label = "Yes, Proceed")
            }
        },
    )
}