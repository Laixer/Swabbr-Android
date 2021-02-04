package com.laixer.swabbr.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker

/**
 *  Extends the [ImagePicker] to select a profile image for us.
 *  Note that this will launch a new Activity. The result of
 *  the image picker
 */
fun ImagePicker.Companion.selectProfileImage(fragment: Fragment) = ImagePicker
    .with(fragment)
    .cropSquare()
    .compress(1024)
    .maxResultSize(512, 512)
    .galleryMimeTypes(  //Exclude gif images
        mimeTypes = arrayOf(
            "image/png",
            "image/jpg",
            "image/jpeg"
        )
    )
    .start()

/**
 *  Should be called in any fragment or activity that calls the
 *  [selectProfileImage] function. Append this call in the onActivityResult
 *  function of your fragment or activity to control what happens when the
 *  image picking process is completed or cancelled.
 *
 *  Note that the [successCallback] will only be called if we have a bitmap.
 *  This means it will not be fired if we fail to decode the bitmap. A log
 *  will be made in this case.
 *
 *  @param context The context object.
 *  @param resultCode Activity result code.
 *  @param data The activity result intent.
 *  @param successCallback What to do in case of a selected image.
 */
fun ImagePicker.Companion.onActivityResult(
    context: Context,
    resultCode: Int,
    data: Intent?,
    successCallback: (selectedBitmap: Bitmap) -> Unit
) {
    when (resultCode) {
        Activity.RESULT_OK -> {
            // If we have data, decode the image, store and assign it.
            data?.let {
                val bitmap = BitmapFactory.decodeFile(ImagePicker.getFilePath(it))

                if (bitmap != null) {
                    successCallback(bitmap)
                } else {
                    // TODO Correct format
                    println("Could not decode bitmap using ImagePicker")
                }
            }
        }
        ImagePicker.RESULT_ERROR -> {
            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        Activity.RESULT_CANCELED -> {
            return
        }
    }
}
