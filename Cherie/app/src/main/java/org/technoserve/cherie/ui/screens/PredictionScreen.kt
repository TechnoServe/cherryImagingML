package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.rgb
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.util.Log
import androidx.compose.ui.platform.LocalContext

import android.os.SystemClock
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.Instant
import org.pytorch.IValue
import org.pytorch.torchvision.TensorImageUtils
import org.technoserve.cherie.Pix2PixModule
import org.technoserve.cherie.Preferences
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory
import org.technoserve.cherie.ui.components.ButtonPrimary
import java.util.Calendar
import kotlin.math.pow
import android.content.Intent





fun distance(col1: IntArray, col2: IntArray): Double{
    val (r1, g1, b1) = col1
    val (r2, g2, b2) = col2
    return (r1 - r2 + 0.0).pow(2.0) + (g1 - g2 + 0.0).pow(2.0) + (b1 - b2 + 0.0).pow(2.0)
}

val refColors: Array<IntArray> = arrayOf(
    intArrayOf(255, 0, 0),        // red
    intArrayOf(0, 255, 0),        // green
    intArrayOf(0, 0, 255),        // blue
    intArrayOf(0, 0, 0),          // black
    intArrayOf(255, 255, 255),    // white
)

enum class ScoreType {
    RIPE, UNDERRIPE, OVERRIPE
}

fun nearestPixel(col1: IntArray): Int{
    var idxClosest = 0
    var minDistance = distance(col1, refColors[idxClosest])

    for (i in 1 until refColors.size){
        val currentDistance = distance(col1, refColors[i])
        if(currentDistance < minDistance){
            minDistance = currentDistance
            idxClosest = i
        }
    }
    val closestColor = refColors[idxClosest]
    return rgb(closestColor[0], closestColor[1], closestColor[2])
}


@Composable
fun PredictionScreen(imageAsByteArray: ByteArray) {
    val bitmap: Bitmap = BitmapFactory.decodeByteArray(imageAsByteArray, 0, imageAsByteArray.size)
    val mask: Bitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val context = LocalContext.current as Activity
    val complete = remember { mutableStateOf(false) }
    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )
    val sharedPrefs by remember { mutableStateOf(Preferences(context)) }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var redCount = 0
    var blueCount = 0
    var greenCount = 0

    fun runModel() {
        val NORM_MEAN_RGB = floatArrayOf(0.5f, 0.5f, 0.5f)
        val NORM_STD_RGB = floatArrayOf(0.5f, 0.5f, 0.5f)
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            NORM_MEAN_RGB, NORM_STD_RGB
        )

        val startTime = SystemClock.elapsedRealtime()
        val outputTensor = Pix2PixModule.mModule!!.forward(IValue.from(inputTensor)).toTensor()
        val inferenceTime = SystemClock.elapsedRealtime() - startTime
        Log.d("ImageSegmentation", "inference time (ms): $inferenceTime")

        val scores = outputTensor.dataAsFloatArray

        val width: Int = bitmap.width
        val height: Int = bitmap.height

        var max = 0f
        var min = 999999f

        for (f in scores) {
            if (f > max) {
                max = f
            }
            if (f < min) {
                min = f
            }
        }

        val delta = (max - min).toInt()
        val pixels = IntArray(width * height * 4)

        for (i in 0 until width * height) {
            val r = ((scores[i] - min) / delta * 255.0f).toInt()
            val g = ((scores[i + width * height] - min) / delta * 255.0f).toInt()
            val b = ((scores[i + width * height * 2] - min) / delta * 255.0f).toInt()

            pixels[i] = nearestPixel(intArrayOf(r, g, b))

            when (pixels[i]) {
                rgb(255, 0, 0) -> redCount++
                rgb(0, 255, 0) -> greenCount++
                rgb(0, 0, 255) -> blueCount++
            }
        }
        mask.setPixels(pixels, 0, width, 0, 0, width, height)
        complete.value = true
        sharedPrefs.generatedPredictions++
    }

    LaunchedEffect(imageAsByteArray) {
        GlobalScope.launch {
            val startTime = System.nanoTime()
            Pix2PixModule.loadModel(context)
            Log.d(
                "Model Task",
                "Loading model took: " + ((System.nanoTime() - startTime) / 1000000) + "mS\n"
            )
            runModel()
            Log.d(
                "Model Task",
                "Running inference took: " + ((System.nanoTime() - startTime) / 1000000) + "mS\n"
            )
        }
    }

    fun showStillRunningMessage(){
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar("Prediction is currently running")
        }
    }


    val onRetry = {
        if(complete.value){
            complete.value = false
            runModel()
        } else {
            showStillRunningMessage()
        }
    }

    fun calculateRipenessScore(scoreType: ScoreType = ScoreType.RIPE): Float {
        return when(scoreType) {
            ScoreType.RIPE -> ((redCount + 0f) / (redCount + greenCount + blueCount + 0f)) * 100
            ScoreType.UNDERRIPE -> ((greenCount + 0f) / (redCount + greenCount + blueCount + 0f)) * 100
            ScoreType.OVERRIPE -> ((blueCount + 0f) / (redCount + greenCount + blueCount + 0f)) * 100
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { Nav(onRetry = onRetry) }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.TopCenter)
                .verticalScroll(scrollState)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Input Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(256.dp)
                    .padding(start = 32.dp, end = 32.dp)
            )


            Spacer(modifier = Modifier.height(16.dp))

            if (complete.value) {
                Image(
                    bitmap = mask.asImageBitmap(),
                    contentDescription = "Mask",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(256.dp)
                        .padding(start = 32.dp, end = 32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ripeness score: ${String.format("%.2f", calculateRipenessScore(ScoreType.RIPE))}%",
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                ButtonPrimary(onClick = {
                    addPrediction(
                        predictionViewModel,
                        bitmap,
                        mask,
                        calculateRipenessScore(ScoreType.RIPE),
                        calculateRipenessScore(ScoreType.UNDERRIPE),
                        calculateRipenessScore(ScoreType.OVERRIPE),
                    )
                    // TODO: Snackbar: Show Prediction saved successfully
                    val returnIntent = Intent()
                    context.setResult(Activity.RESULT_OK, returnIntent)
                    context.finish()
                }, label = "Save")
            } else {
                Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Spacer(modifier = Modifier.height(32.dp))
                    LinearProgressIndicator()
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Predicting ripeness score...",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun Nav(onRetry: () -> Unit) {
    val context = LocalContext.current as Activity
    TopAppBar(
        title = {
            Text(
                text = "Prediction Screen",
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
        actions = {
            IconButton(onClick = {
                GlobalScope.launch {
                    onRetry()
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    )
}

fun addPrediction(
    predictionViewModel: PredictionViewModel,
    inputImage: Bitmap,
    mask: Bitmap,
    ripe: Float,
    underripe: Float,
    overripe: Float
) {
    val prediction = Prediction(
        inputImage,
        mask,
        ripe,
        overripe,
        underripe,
        synced = false,
        createdAt = Instant.now().millis
    )
    predictionViewModel.addPrediction(prediction)
}