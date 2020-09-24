package com.u3coding.myimageloader.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*


class FileImageCache(private val context: Context): ImageCache {

   override fun cacheImg(bitmap: Bitmap?,name:String) {
       if (bitmap != null)
        try {
            val file = File(context.cacheDir, name)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun getCacheImage(name:String): Bitmap? {
        val file = File(context.cacheDir, name)
        var bitmap: Bitmap? = null
        if (file.length() > 0) {
            val inputStream: InputStream = FileInputStream(file)
             bitmap = BitmapFactory.decodeStream(inputStream)
        }
        return bitmap
    }

}