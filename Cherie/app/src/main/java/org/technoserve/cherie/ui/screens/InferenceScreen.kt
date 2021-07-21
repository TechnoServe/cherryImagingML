package org.technoserve.cherie.ui.screens

import android.R.attr
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
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.CallSuper
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
import androidx.compose.ui.draw.shadow
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import org.technoserve.cherie.BuildConfig
import org.technoserve.cherie.R
import java.io.File
import java.io.IOException
import android.R.attr.angle
import android.graphics.*


fun getRandomFilepath(
    context: Context,
    extension: String,
    directory: String = Environment.DIRECTORY_PICTURES
): String {
    return "${context.getExternalFilesDir(directory)?.absolutePath}/${System.currentTimeMillis()}.$extension"
}

fun getRandomUri(
    context: Context,
    extension: String,
    directory: String = Environment.DIRECTORY_PICTURES
): Uri {
    return getUriFromPath(context, getRandomFilepath(context, extension, directory))
}

fun getUriFromPath(context: Context, path: String): Uri {
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        File(path)
    )
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




    val startForResultToLoadImage =
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

    val takePicture =
        rememberLauncherForActivityResult(
            contract =
            ActivityResultContracts.TakePicture()
        ) { imageCaptured ->
            if (imageCaptured) {
                // Do stuff with your Uri here
            }
        }

    val launchCamera: () -> Unit = {
//        val uri = getRandomUri(context, ".jpg", "Cherie")

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
            startForResultToLoadImage.launch(intent)
        }
    }

    val loadFromGallery: () -> Unit = {
        selectImageLauncher.launch("image/*")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
    ) {
        HeaderWithIcon()
//        imageUri?.let {
//            if (Build.VERSION.SDK_INT < 28) {
//                bitmap.value = MediaStore.Images
//                    .Media.getBitmap(context.contentResolver, it.value)
//
//            } else {
//                val source = it.value?.let { it1 ->
//                    ImageDecoder
//                        .createSource(context.contentResolver, it1)
//                }
//                bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
//            }
//
//            bitmap.value?.let { btm ->
//                Image(
//                    bitmap = btm.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier.size(200.dp)
//                )
//            }
//        }
        bitmap.value?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        }
        if (bitmap.value == null) {
            RowLayout(loadFromGallery, launchCamera)
        }

    }
}


@Composable
fun HeaderWithIcon() {
    Image(
        painter = painterResource(id = R.drawable.cherry_white),
        contentDescription = "",
        contentScale = ContentScale.Inside,
        modifier = Modifier
            .height(240.dp)
            .padding(top = 60.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = "Cherie", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(60.dp))
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
