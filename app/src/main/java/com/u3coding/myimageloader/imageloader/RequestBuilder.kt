package com.u3coding.myimageloader.imageloader

import android.content.Context
import android.widget.ImageView


object RequestBuilder {
    data class ImageParams(
         var roundPx: Float = 0f,
         val emptyPlaceHolderId: Int = -1,
         var placeHolder: Int = emptyPlaceHolderId,
         var imageURL: String = "",
         var imageMaxSideSize: Float = -1f,
         var useCache: Boolean = true,
         var context: Context? = null)
    private lateinit var params:ImageParams
    private val memoryCache = MemoryLRUCache()
    fun withContext(context: Context): RequestBuilder {
        params = ImageParams()
        params.context = context
        return this
    }

    fun useCache(useCache: Boolean): RequestBuilder {
        params.useCache = useCache
        return this
    }

    fun placeholder(placeholder: Int): RequestBuilder {
        params.placeHolder = placeholder
        return this
    }

    fun load(url: String): RequestBuilder {
        params.imageURL = url
        return this
    }
    fun round(round:Float): RequestBuilder {
        params.roundPx = round
        return this
    }
    fun into(imageView: ImageView) {
        val realLoader = RealImageLoader(params, memoryCache)
        realLoader.loadImage(imageView)
    }
    fun adjustImageScale(imageMaxSideSize:Float): RequestBuilder {
        params.imageMaxSideSize = imageMaxSideSize
        return this
    }

}