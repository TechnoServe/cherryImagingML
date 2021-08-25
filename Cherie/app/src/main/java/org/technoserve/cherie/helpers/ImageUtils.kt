package org.technoserve.cherie.helpers

import android.content.Context
import android.net.Uri
import java.io.IOException
import java.io.FileNotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import java.io.FileOutputStream
import java.io.File


class ImageUtils {
    companion object {
        fun createTempBitmapUri(context: Context, bmp: Bitmap, imgName: String): Uri {
            val file = File(context.cacheDir, "$imgName.png")
            var outStream: FileOutputStream?
            try {
                outStream = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.flush()
                outStream.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return Uri.fromFile(file)
        }

        fun combineBitmaps(left: Bitmap, right: Bitmap): Bitmap {
            // Get the size of the images combined side by side.
            val width = left.width + right.width
            val height = if (left.height > right.height) left.height else right.height

            // Create a Bitmap large enough to hold both input images and a canvas to draw to this
            // combined bitmap.
            val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(combined)

            // Render both input images into the combined bitmap and return it.
            canvas.drawBitmap(left, 0f, 0f, null)
            canvas.drawBitmap(right, left.width.toFloat(), 0f, null)
            return combined
        }
    }
}