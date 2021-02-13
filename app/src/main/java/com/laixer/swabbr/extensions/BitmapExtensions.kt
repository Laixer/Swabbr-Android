package com.laixer.swabbr.utils

import android.graphics.Bitmap

// TODO throw if null
/**
 *  Encodes a bitmap image to a base 64 encoded string.
 */
fun Bitmap.encodeToBase64(): String = encodeImageToBase64(convertBitmapToByteArray(this))
