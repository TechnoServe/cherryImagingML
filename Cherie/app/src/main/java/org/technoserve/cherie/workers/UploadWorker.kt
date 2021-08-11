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


const val WORKER_IMAGE_NAME_KEY = "WORKER_IMAGE_NAME_KEY"
const val WORKER_IMAGE_URI_KEY = "WORKER_IMAGE_URI_KEY"

class UploadWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        // val fileName = inputData.getStringArray()
        val fileName = inputData.getString(WORKER_IMAGE_NAME_KEY)
        val imageUri = Uri.parse(inputData.getString(WORKER_IMAGE_URI_KEY))
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(imageUri).addOnSuccessListener {
            Log.d("UPLOAD", "Uploaded successfully" + it.uploadSessionUri.toString())
            displayNotification("Image Sync Complete", "3 Images Uploaded Successfully")
            Result.success()
        }.addOnFailureListener {
            Log.d("UPLOAD", "Upload Failed")
            displayNotification("Image Sync Failed", "Retrying...")
            Result.retry()
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
