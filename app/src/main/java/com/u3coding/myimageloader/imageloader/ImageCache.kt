package com.u3coding.myimageloader.imageloader

import android.graphics.Bitmap

interface ImageCache{
    fun cacheImg(bitmap: Bitmap?,name:String)
    fun getCacheImage(name:String): Bitmap?
}