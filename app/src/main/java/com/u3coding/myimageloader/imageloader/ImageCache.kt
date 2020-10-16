package com.u3coding.myimageloader.imageloader

import android.graphics.Bitmap
import android.widget.ImageView

interface ImageCache{
    fun cacheImg(bitmap: Bitmap?,name:String)
    fun getCacheImage(name:String): Bitmap?
}