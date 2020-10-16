package com.u3coding.myimageloader.imageloader

import android.graphics.Bitmap
import android.util.LruCache

class MemoryLRUCache() :ImageCache{
    private var lruCache:LruCache<String,Bitmap>
    init{
        val maxMemory = Runtime.getRuntime().maxMemory()/1024
        val cacheSize = maxMemory / 8
        lruCache = object:LruCache<String,Bitmap>(cacheSize.toInt()){
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                return bitmap!!.rowBytes*bitmap.height /1024
            }
        }
    }
    override fun cacheImg(bitmap: Bitmap?, name: String) {
        if(getCacheImage(name) == null){
            if(bitmap!=null)
            lruCache.put(name,bitmap)
        }
    }

    override fun getCacheImage(name: String): Bitmap? {
        return lruCache.get(name)
    }
}