package com.u3coding.myimageloader.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt

 class BitmapCompressor(private val context:Context) {
    private fun getImageViewSize(imageView: ImageView): Pair<Int, Int> {
        val displayMetrics = context.resources?.displayMetrics
        val layoutParams = imageView.layoutParams
        var viewWidth = imageView.width
        var viewHeight = imageView.height
        if (viewWidth <= 0) {
            viewWidth = layoutParams.width
        }
        if (viewWidth <= 0) {
            viewWidth = imageView.maxWidth
        }
        if (viewWidth <= 0) {
            viewWidth = displayMetrics?.widthPixels!!
        }
        if (viewHeight <= 0) {
            viewHeight = layoutParams.height
        }
        if (viewHeight <= 0) {
            viewHeight = imageView.maxHeight
        }
        if (viewHeight <= 0) {
            viewHeight = displayMetrics?.heightPixels!!
        }
        return Pair(viewWidth, viewHeight)
    }

    private fun getInSampleSize(options: BitmapFactory.Options, imageView: ImageView): Int {
        var inSampleSize = 1
        val (viewWidth, viewHeight) = getImageViewSize(imageView)

        val outWidth = options.outWidth
        val outHeight = options.outHeight

        if (outWidth > viewWidth || outHeight > viewHeight) {
            val widthRadio = (outWidth / viewWidth).toDouble().roundToInt()
            val heightRadio = (outHeight / viewHeight).toDouble().roundToInt()

            inSampleSize = if (widthRadio > heightRadio) widthRadio else heightRadio
        }

        return inSampleSize
    }


    fun getCompressBitmap(input: InputStream, imageView: ImageView): Bitmap? {
        val stream = ByteArrayOutputStream()
        val bufferSize = 1024
        try {
            val buffer = ByteArray(bufferSize)
            var len: Int
            while (input.read(buffer).also { len = it } > -1) {
                stream.write(buffer, 0, len)
            }
            stream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val sizeInputStream: InputStream = ByteArrayInputStream(stream.toByteArray())
        val bitmapInputStream: InputStream = ByteArrayInputStream(stream.toByteArray())

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(sizeInputStream, null, options)

        options.inSampleSize = getInSampleSize(options, imageView)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(bitmapInputStream, null, options)

    }
}