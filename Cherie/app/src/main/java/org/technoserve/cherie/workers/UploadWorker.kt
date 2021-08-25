package org.technoserve.cherie.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import org.technoserve.cherie.R

import androidx.core.app.NotificationCompat

import android.app.NotificationManager

import android.app.NotificationChannel
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.technoserve.cherie.Preferences
import org.technoserve.cherie.database.AppDatabase

const val TAG = "TAG"

const val WORKER_IMAGE_NAMES_KEY = "WORKER_IMAGE_NAMES_KEY"
const val WORKER_IMAGE_URIS_KEY = "WORKER_IMAGE_URIS_KEY"
const val WORKER_PREDICTION_IDS_KEY = "WORKER_PREDICTION_IDS_KEY"

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val sharedPrefs = Preferences(applicationContext)
    private val db = Firebase.firestore
    private val region = applicationContext.getString(R.string.app_region)

    override fun doWork(): Result {

        val predictionDAO = AppDatabase.getInstance(applicationContext).predictionsDAO()

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val fileNames = inputData.getStringArray(WORKER_IMAGE_NAMES_KEY)
        val imageUris = inputData.getStringArray(WORKER_IMAGE_URIS_KEY)
        val predictionIds = inputData.getLongArray(WORKER_PREDICTION_IDS_KEY)

        if (fileNames != null) {
            displayNotification(
                "Starting Upload",
                "${fileNames.size} will be uploaded in the background"
            )
            var successfulUploads = 0
            var failedOnce = false
            for (i in fileNames.indices) {
                val fileName = fileNames[i]
                val imageUri = Uri.parse(imageUris?.get(i))
                val predictionId = predictionIds?.get(i)
                val storageReference =
                    FirebaseStorage.getInstance().getReference("images/${region.lowercase()}/$fileName")
                storageReference.putFile(imageUri).addOnSuccessListener {
                    Log.d("UPLOAD", "Uploaded successfully" + it.uploadSessionUri.toString())
                    GlobalScope.launch {
                        if (predictionId != null) {
                            predictionDAO.updateSyncStatus(predictionId)
                        }
                    }
                    successfulUploads++
                    sharedPrefs.uploadedPredictions++
                    if (successfulUploads == fileNames.size) {
                        displayNotification(
                            "Image Sync Complete",
                            "${fileNames.size} images uploaded successfully"
                        )
                    }
                    GlobalScope.launch {
                        val predictions =
                            predictionId?.let { predictionDAO.getRawPredictionById(it) }
                        if (predictions != null && predictions.isNotEmpty()) {
                            val prediction = predictions[0]

                            storageReference.downloadUrl.addOnCompleteListener { it2 ->
                                if (it2.isSuccessful) {

                                    Log.d("TAG", "Uploaded" + it2.result.toString())


                                    val payload = hashMapOf(
                                        "predictionId" to predictionId,
                                        "imageUri" to it2.result.toString(),
                                        "ripe" to prediction.ripe,
                                        "underripe" to prediction.underripe,
                                        "overripe" to prediction.overripe,
                                        "predictedAt" to prediction.createdAt,
                                        "region" to  region
                                    )

                                    db.collection("users/$userId/predictions")
                                        .add(payload)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d(
                                                TAG,
                                                "DocumentSnapshot added with ID: ${documentReference.id}"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding document", e)
                                        }
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    Log.d("UPLOAD", "Upload Failed")
                    if (!failedOnce) {
                        displayNotification("Image Sync Failed", "Retrying...")
                        failedOnce = true
                    }
                }
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    /*
     * The method is doing nothing but only generating
     * a simple notification
     * If you are confused about it
     * you should check the Android Notification Tutorial
     * */
    private fun displayNotification(title: String, task: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "cherie",
                "Cherie Prediction Sync",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, "cherie")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(1, notification.build())
    }
}
