package com.u3coding.myimageloader.imageloader

import android.graphics.*
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class RealImageLoader(private val params: RequestBuilder.ImageParams) {
    private var fileCache = FileImageCache(params.context!!)
    private var bitmapCompressor = BitmapCompressor(params.context!!)

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
                    bitmap = fileCache.getCacheImage(getFileName())
                }
                if (bitmap == null) {
                    bitmap = getNetImg(imageView)
                }
                if (params.roundPx != 0f) {
                    if (bitmap != null)
                        bitmap = BitmapRounder.getRoundedCornerBitmap(bitmap!!, params.roundPx)
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

    private fun getNetImg(imageView: ImageView): Bitmap? {
        var bitmap: Bitmap? = null
        val url = URL(params.imageURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        val code = connection.responseCode
        if (code == 200) {
            val inputStream = connection.inputStream
            bitmap = bitmapCompressor.getCompressBitmap(inputStream, imageView)
            if (params.useCache)
                fileCache.cacheImg(bitmap,getFileName())
            inputStream.close()
        } else {
            Log.e("NetImageView", "server error")
        }
        return bitmap
    }

    private fun getFileName(): String {
        var name = ""
        val strings = params.imageURL.split("/")
        for (s in strings) {
            name += s
        }
        return name
    }


    private fun loadPlaceHolderImg(imageView: ImageView) {
        if (params.placeHolder != params.emptyPlaceHolderId) {
            var bitmap = BitmapFactory.decodeResource(params.context?.resources, params.placeHolder)
            if (params.roundPx != 0f) {
                if (bitmap != null)
                    bitmap =BitmapRounder.getRoundedCornerBitmap(bitmap, params.roundPx)
            }
            imageView.setImageBitmap(bitmap)
        }
    }
}