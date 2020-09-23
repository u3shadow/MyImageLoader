package com.u3coding.myimageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt


class NetImageView(context: Context, attribut: AttributeSet) :
    AppCompatImageView(context, attribut) {
    private val emptyPlaceHolderId = -1
    private var placeHolder: Int = emptyPlaceHolderId
    private var imageURL: String = ""
    private var useCache = true
    fun setPlaceHolder(placeHolder: Int) {
        this.placeHolder = placeHolder
    }

    fun setUrl(path: String) {
        var bitmap: Bitmap? = null
        imageURL = path
        loadPlaceHolderImg()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (useCache) {
                    bitmap = getCacheImage()
                }
                if (bitmap == null) {
                    bitmap = loadNetImg()
                }
            } catch (e: IOException) {
                Log.e("NetImageView", "net error")
            }
            withContext(Dispatchers.Main) {
                if (bitmap != null)
                    setImageBitmap(bitmap)
                else loadPlaceHolderImg()
            }
        }
    }

    private fun loadNetImg(): Bitmap? {
        var bitmap: Bitmap? = null
        val url = URL(imageURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        val code = connection.responseCode
        if (code == 200) {
            val inputStream = connection.inputStream
            bitmap = getCompressBitmap(inputStream)
            if (useCache)
            cacheImg(bitmap)
            inputStream.close()
        } else {
            Log.e("NetImageView", "server error")
        }
        return bitmap
    }

    private fun loadPlaceHolderImg() {
        if (placeHolder != emptyPlaceHolderId) {
            val bitmap = BitmapFactory.decodeResource(context.resources, placeHolder)
            setImageBitmap(bitmap)
        }
    }

    private fun getImgViewWidth(): Int {
        val displayMetrics = context.resources.displayMetrics
        val layoutParams = layoutParams
        var viewWidth = width
        if (viewWidth <= 0) {
            viewWidth = layoutParams.width
        }
        if (viewWidth <= 0) {
            viewWidth = maxWidth
        }
        if (viewWidth <= 0) {
            viewWidth = displayMetrics.widthPixels
        }
        return viewWidth
    }

    private fun getImgViewHeight(): Int {
        val displayMetrics = context.resources.displayMetrics
        val layoutParams = layoutParams
        var viewHeight = height
        if (viewHeight <= 0) {
            viewHeight = layoutParams.height
        }
        if (viewHeight <= 0) {
            viewHeight = maxHeight
        }
        if (viewHeight <= 0) {
            viewHeight = displayMetrics.heightPixels
        }
        return viewHeight
    }

    private fun getInSampleSize(options: BitmapFactory.Options): Int {
        var inSampleSize = 1
        val viewWidth = getImgViewWidth()
        val viewHeight = getImgViewHeight()

        val outWidth = options.outWidth
        val outHeight = options.outHeight

        if (outWidth > viewWidth || outHeight > viewHeight) {
            val widthRadio = (outWidth / viewWidth).toDouble().roundToInt()
            val heightRadio = (outHeight / viewHeight).toDouble().roundToInt()

            inSampleSize = if (widthRadio > heightRadio) widthRadio else heightRadio
        }

        return inSampleSize
    }

    private fun getCompressBitmap(input: InputStream): Bitmap {
        val stream = ByteArrayOutputStream()
        try {
            val buffer = ByteArray(1024)
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

        options.inSampleSize = getInSampleSize(options)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(bitmapInputStream, null, options)!!
    }

    private fun getFileName(): String {
        var name = ""
        val strings = imageURL.split("/")
        for (s in strings) {
            name += s
        }
        return name
    }

    private fun cacheImg(bitmap: Bitmap) {
        try {
            val file = File(context.cacheDir, getFileName())
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCacheImage(): Bitmap? {
        val file = File(context.cacheDir, getFileName())
        var bitmap: Bitmap? = null
        if (file.length() > 0) {
            val inputStream: InputStream = FileInputStream(file)
            bitmap = getCompressBitmap(inputStream)
        }
        return bitmap
    }

}