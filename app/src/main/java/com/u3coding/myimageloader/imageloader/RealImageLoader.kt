package com.u3coding.myimageloader.imageloader

import android.graphics.*
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.solver.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class RealImageLoader(private val params: RequestBuilder.ImageParams) {
    private val fileCache = FileImageCache(params.context!!)
    fun loadImage(imageView: ImageView) {
        if (params.context == null) {
            Log.e("ImageLoader", "Empty context")
            return
        }
        var bitmap: Bitmap? = null
        loadPlaceHolderImg(imageView)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (params.useCache) {
                    bitmap = getCacheImage(imageView)
                }
                if (bitmap == null) {
                    bitmap = loadNetImg(imageView)
                }
                if (params.roundPx != 0f) {
                    if (bitmap != null)
                        bitmap = getRoundedCornerBitmap(bitmap!!, params.roundPx)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("NetImageView", "Load image error")
            }
            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)

                }
            }
        }
    }

    private fun loadNetImg(imageView: ImageView): Bitmap? {
        var bitmap: Bitmap? = null
        val url = URL(params.imageURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        val code = connection.responseCode
        if (code == 200) {
            val inputStream = connection.inputStream
            bitmap = getCompressBitmap(inputStream, imageView)
            if (params.useCache)
                fileCache.cacheImg(bitmap,get)
                cacheImg(bitmap)
            inputStream.close()
        } else {
            Log.e("NetImageView", "server error")
        }
        return bitmap
    }


    private fun getImageViewSize(imageView: ImageView): Pair<Int, Int> {
        val displayMetrics = params.context?.resources?.displayMetrics
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


    private fun getCompressBitmap(input: InputStream, imageView: ImageView): Bitmap {
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
        return BitmapFactory.decodeStream(bitmapInputStream, null, options)!!
    }


    private fun getCacheImage(imageView: ImageView,fileName:String): Bitmap? {
        val file = File(params.context?.cacheDir, fileName)
        var bitmap: Bitmap? = null
        if (file.length() > 0) {
            val inputStream: InputStream = FileInputStream(file)
            bitmap = getCompressBitmap(inputStream, imageView)
        }
        return bitmap
    }


    private fun getRoundedCornerBitmap(bitmap: Bitmap, round: Float): Bitmap? {
        return try {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            val rectF = RectF(
                Rect(
                    0, 0, bitmap.width,
                    bitmap.height
                )
            )
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.BLACK
            canvas.drawRoundRect(rectF, round, round, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val src = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            canvas.drawBitmap(bitmap, src, rect, paint)
            output
        } catch (e: Exception) {
            bitmap
        }
    }

    private fun loadPlaceHolderImg(imageView: ImageView) {
        if (params.placeHolder != params.emptyPlaceHolderId) {
            var bitmap = BitmapFactory.decodeResource(params.context?.resources, params.placeHolder)
            if (params.roundPx != 0f) {
                if (bitmap != null)
                    bitmap = getRoundedCornerBitmap(bitmap, params.roundPx)
            }
            imageView.setImageBitmap(bitmap)
        }
    }
}