package org.technoserve.cherie.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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
import androidx.navigation.NavController
import org.technoserve.cherie.BuildConfig
import org.technoserve.cherie.R
import java.io.File
import java.io.IOException
import android.graphics.*
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import org.technoserve.cherie.PredictionActivity
import org.technoserve.cherie.ui.navigation.NavigationItem
import java.io.ByteArrayOutputStream

fun get512Bitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    var bmp = bitmap;
    bmp = if (width >= height) {
        Bitmap.createBitmap(bmp, width / 2 - height / 2, 0, height, height)
    } else {
        Bitmap.createBitmap(bmp, 0, height / 2 - width / 2, width, width)
    }
    bmp = Bitmap.createScaledBitmap(bmp, 512, 512, true)
    return bmp
}


@Composable
fun InferenceScreen(navController: NavController) {
    var imageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val currentPhotoPath = remember { mutableStateOf<String>("") }


    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if(uri != null){
            imageUri.value = uri
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, uri)

            } else {
                val source = uri?.let {
                    ImageDecoder
                        .createSource(context.contentResolver, it)
                }
                bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
            }

            val bitmapWidth = bitmap.value?.width!!
            val bitmapHeight = bitmap.value?.height!!
            var bmp = bitmap.value!!
            // TODO: Resize to 512 x 512
            bitmap.value = get512Bitmap(bmp, bitmapWidth, bitmapHeight)
        }
    }

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


    val takePicture =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val selectedImage: Uri? = result.data?.data
                    if (selectedImage != null) {
                        // From Gallery
                    } else {
                        // From Camera
                        val bmp = BitmapFactory.decodeStream(imageUri.value?.let {
                            context?.contentResolver?.openInputStream(
                                it
                            )
                        })

                        val matrix = Matrix()
                        matrix.postRotate(90.0f)
                        bitmap.value = Bitmap.createBitmap(
                            bmp,
                            0,
                            0,
                            bmp.width,
                            bmp.height,
                            matrix,
                            true
                        )
                    }
                } catch (error: Exception) {
                    Log.d("TAG==>>", "Error : ${error.localizedMessage}")
                }
            }
        }


    val launchCamera: () -> Unit = {
        val photoURI: Uri? = context?.let {
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
            takePicture.launch(intent)
        }
    }

    val loadFromGallery: () -> Unit = {
        selectImageLauncher.launch("image/*")
    }

    val dismissDialog: () -> Unit = {
        bitmap.value = null
    }
    val proceedToPredictionScreen: () -> Unit = {

        val stream = ByteArrayOutputStream()
        bitmap.value?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imgAsByteArray: ByteArray = stream.toByteArray()

        // Wipe state
        imageUri.value = null
        bitmap.value = null
        currentPhotoPath.value = ""

        val startTime = System.nanoTime()

        val intent = PredictionActivity.newIntent(context, imgAsByteArray)
        Log.d("Intent creation", "TASK took : " +  ((System.nanoTime()-startTime)/1000000)+ "mS\n")
        // Image from camera is too large and crashes the when the next line is run
        // The Binder transaction buffer has a limited fixed size of 1Mb
        context.startActivity(intent)
        Log.d("Activity Move", "TASK took : " +  ((System.nanoTime()-startTime)/1000000)+ "mS\n")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
    ) {
        HeaderWithIcon(navController)
        RowLayout(loadFromGallery, launchCamera)
        bitmap.value?.let {
            FullScreenDialog(bitmap.value != null, it, dismissDialog, proceedToPredictionScreen)
        }
    }
}


@Composable
fun HeaderWithIcon(navController: NavController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(32.dp)
        ) {
            FloatingActionButton(
                contentColor = MaterialTheme.colors.primary,
                backgroundColor = Color.White,
                onClick = {
                    navController.navigate(NavigationItem.Profile.route)
                },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp
                ),
                modifier = Modifier.size(48.dp, 48.dp)
            ) {
                Icon(Icons.Outlined.Person, "", tint = MaterialTheme.colors.primaryVariant)
            }
        }

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
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(32.dp)
                    ) {
                        FloatingActionButton(
                            contentColor = MaterialTheme.colors.onSurface,
                            backgroundColor = Color.White,
                            onClick = { onClose() }
                        ) {
                            Icon(Icons.Outlined.Close, "", tint = Color.Black)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Proceed with prediction?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = MaterialTheme.colors.onSurface,
                        )
                        Spacer(modifier = Modifier.height(32.dp))
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
                            .padding(bottom = 36.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { onClose() },
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
                            onClick = { onConfirm() },
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
        Spacer(modifier = Modifier.height(64.dp))
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
                .padding(start = 32.dp, top = 48.dp, end = 32.dp, bottom = 80.dp),
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
                Text(text = "Take picture", color = MaterialTheme.colors.onSurface)
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

@Composable
fun ColumnLayout() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(color = MaterialTheme.colors.background)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val haptic = LocalHapticFeedback.current
        FloatingActionButton(
            contentColor = MaterialTheme.colors.onSurface,
            backgroundColor = Color.White,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        ) {
            Icon(Icons.Outlined.PhotoCamera, "", tint = MaterialTheme.colors.primary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Take picture", color = MaterialTheme.colors.onSurface)

        Spacer(modifier = Modifier.height(42.dp))

        FloatingActionButton(
            contentColor = MaterialTheme.colors.onSurface,
            backgroundColor = Color.White,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        ) {
            Icon(Icons.Outlined.Image, "", tint = Color.Blue)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Load from gallery", color = MaterialTheme.colors.onSurface)
    }
}
