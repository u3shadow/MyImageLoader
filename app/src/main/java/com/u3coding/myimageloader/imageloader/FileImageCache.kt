package com.u3coding.myimageloader.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*


class FileImageCache() : ImageCache {
    var context:Context? = null
    @Synchronized
    override fun cacheImg(bitmap: Bitmap?, name: String) {
        if (context == null){
            Log.e("FileImageCache","context is null")
            return
        }
        if (bitmap != null)
            try {
                val file = File(context?.cacheDir, name)
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
    }

    @Synchronized
    override fun getCacheImage(name: String): Bitmap? {
        if (context == null){
            Log.e("FileImageCache","context is null")
            return null
        }
        val file = File(context?.cacheDir, name)
        var bitmap: Bitmap? = null
        if (file.length() > 0) {
            val inputStream: InputStream = FileInputStream(file)
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
        return bitmap
    }

}