package com.u3coding.myimageloader.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
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
                    bitmap = fileCache.getCacheImage(getCacheFileName())
                }
                if (bitmap == null) {
                    bitmap = getNetImg(imageView)
                }
                if (bitmap != null)
                bitmap = roundBitmap(bitmap)
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

    private fun loadPlaceHolderImg(imageView: ImageView) {
        if (params.placeHolder != params.emptyPlaceHolderId) {
            var bitmap = BitmapFactory.decodeResource(params.context?.resources, params.placeHolder)
            bitmap = roundBitmap(bitmap)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun roundBitmap(bitmap: Bitmap?): Bitmap? {
        var roundBitmap = bitmap
        if (params.roundPx != 0f) {
            if (roundBitmap != null)
                roundBitmap = BitmapRounder.getRoundedCornerBitmap(roundBitmap, params.roundPx)
        }
        return roundBitmap
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
            if(bitmap != null)
            bitmap = changeScale(bitmap)
            if (params.useCache)
                fileCache.cacheImg(bitmap, getCacheFileName())
            inputStream.close()
        } else {
            Log.e("NetImageView", "server error")
        }
        connection.inputStream
        return bitmap
    }

    private fun getCacheFileName(): String {
        var name = ""
        val strings = params.imageURL.split("/")
        for (s in strings) {
            name += s
        }
        return name
    }

    private fun changeScale(bitmap: Bitmap):Bitmap{
        var mBitmap = bitmap
        if (params.imageMaxSideSize > 0){
            var height = bitmap.height
            var width = bitmap.width
            if(width >= height){
                width = params.imageMaxSideSize.toInt()
                height = (params.imageMaxSideSize*(bitmap.height.toFloat()/bitmap.width)).toInt()
            }else{
                height = params.imageMaxSideSize.toInt()
                width = (params.imageMaxSideSize*(bitmap.width.toFloat()/bitmap.height)).toInt()
            }
          mBitmap = zoomImg(bitmap,width,height)
        }
        return mBitmap
    }
    private fun zoomImg(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap{
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
    }

}