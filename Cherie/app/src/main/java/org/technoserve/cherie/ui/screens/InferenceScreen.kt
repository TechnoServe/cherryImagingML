package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import org.technoserve.cherie.R
import java.io.File
import java.io.IOException
import android.graphics.*
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.technoserve.cherie.PredictionActivity
import org.technoserve.cherie.ui.components.ButtonPrimary
import org.technoserve.cherie.ui.navigation.NavigationItem
import java.io.ByteArrayOutputStream


@Composable
fun InferenceScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    homeScope: CoroutineScope
) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val currentPhotoPath = remember { mutableStateOf("") }
    val dialogIsVisible = remember { mutableStateOf(false) }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        // val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_TEMP_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath.value = absolutePath
        }
    }

    val cropImage = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent: Uri? = result.uriContent
            bitmap.value = BitmapFactory.decodeStream(imageUri.value?.let {
                uriContent?.let { it1 -> context?.contentResolver?.openInputStream(it1) }
            })
            dialogIsVisible.value = true
        } else {
            val exception = result.error
            homeScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Error: Something went wrong")
            }
            Log.d("CHERIE@CROP", "Error : ${exception?.localizedMessage}")
        }
    }

    fun startCrop() {
        cropImage.launch(
            options(uri = imageUri.value) {
                setGuidelines(CropImageView.Guidelines.ON)
                setFixAspectRatio(true)
                setAspectRatio(1, 1)
                setInitialCropWindowPaddingRatio(0f)
                setActivityTitle("Resize Image")
                setRequestedSize(512, 512)
                setMinCropResultSize(512, 512)
                setOutputCompressQuality(80)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                setCropMenuCropButtonIcon(R.drawable.done)
            }
        )
    }

    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri.value = uri
            startCrop()
        }
    }

    val loadFromGallery: () -> Unit = {
        selectImageLauncher.launch("image/*")
    }

    val takePicture =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    startCrop()
                } catch (error: Exception) {
                    Log.d("CHERIE@CAMERA", "Error : ${error.localizedMessage}")
                }
            }
        }

    val launchCamera: () -> Unit = {
        val photoURI: Uri? = context.let {
            createImageFile().let { it1 ->
                FileProvider.getUriForFile(
                    it,
                    context.applicationContext.packageName.toString() + ".provider",
                    it1
                )
            }
        }
        photoURI?.let {
            imageUri.value = it

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra("android.intent.extra.quickCapture", true)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
            takePicture.launch(intent)
        }
    }

    val dismissDialog: () -> Unit = {
        val backToCrop = false
        if(backToCrop){
            // Alternate flow - Take user back to Crop Modal
            startCrop()
            dialogIsVisible.value = false
        } else {
            // Wipe state
            imageUri.value = null
            bitmap.value = null
            currentPhotoPath.value = ""

            dialogIsVisible.value = false
        }
    }

    val runPrediction =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                homeScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Saved Successfully")
                }
                navController.navigate(NavigationItem.Logs.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                Log.d("TAG", "Got result OK - Saving Prediction")
            } else {
                Log.d("TAG", "Back button was pressed - Save Cancelled")
            }
        }

    val proceedToPredictionScreen: () -> Unit = {

        val stream = ByteArrayOutputStream()
        bitmap.value?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imgAsByteArray: ByteArray = stream.toByteArray()

        val intent = PredictionActivity.newIntent(context, imgAsByteArray)
        runPrediction.launch(intent)

        dismissDialog()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            HeaderWithIcon()
        }
        Box(modifier = Modifier.weight(1f)) {
            RowLayout(loadFromGallery, launchCamera)
        }
        bitmap.value?.let {
            FullScreenDialog(dialogIsVisible.value, it, dismissDialog, proceedToPredictionScreen)
        }
    }
}


@Composable
fun HeaderWithIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.cherry_white),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .height(240.dp)
                    .padding(top = 60.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Cherie",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}


@Composable
fun FullScreenDialog(
    showDialog: Boolean,
    image: Bitmap,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onClose) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
                color = MaterialTheme.colors.background
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
//                        Text(
//                            text = "Proceed with prediction?",
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 25.sp,
//                            color = MaterialTheme.colors.onSurface,
//                        )
//                        Spacer(modifier = Modifier.height(32.dp))
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, end = 32.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 72.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val tint =
                            if (isSystemInDarkTheme()) Color.White else MaterialTheme.colors.primary
                        IconButton(
                            onClick = { onClose() },
                            modifier = Modifier.requiredWidth(160.dp),
                        ) {
                            Icon(
                                Icons.Filled.Replay,
                                contentDescription = "retake",
                                tint = tint,
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = { onConfirm() },
                            modifier = Modifier.requiredWidth(160.dp),
                        ) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = "Use this Image",
                                tint = tint,
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RowLayout(loadFromGallery: () -> Unit, launchCamera: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(color = MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Capture Image For Prediction",
            color = MaterialTheme.colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 32.dp, top = 48.dp, end = 32.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            val haptic = LocalHapticFeedback.current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                FloatingActionButton(
                    contentColor = MaterialTheme.colors.onSurface,
                    backgroundColor = Color.White,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        launchCamera()
                    }
                ) {
                    Icon(Icons.Outlined.PhotoCamera, "", tint = MaterialTheme.colors.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Take picture",
                    color = MaterialTheme.colors.onSurface
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                FloatingActionButton(
                    contentColor = MaterialTheme.colors.onSurface,
                    backgroundColor = Color.White,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        loadFromGallery()
                    }
                ) {
                    Icon(Icons.Outlined.Image, "", tint = Color.Blue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Load from gallery", color = MaterialTheme.colors.onSurface)
            }

        }
    }
}