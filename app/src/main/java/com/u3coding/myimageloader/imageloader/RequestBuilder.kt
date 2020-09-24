package com.u3coding.myimageloader.imageloader

import android.content.Context
import android.widget.ImageView


class RequestBuilder {
    data class ImageParams(
         var roundPx: Float = 0f,
         val emptyPlaceHolderId: Int = -1,
         var placeHolder: Int = emptyPlaceHolderId,
         var imageURL: String = "",
         var imageMaxSideSize: Float = -1f,
         var useCache: Boolean = true,
         var context: Context? = null)
     private var params = ImageParams()
    fun withContext(context: Context): RequestBuilder {
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
        val realLoader = RealImageLoader(params)
        realLoader.loadImage(imageView)
    }
    fun adjustImageScale(imageMaxSideSize:Float): RequestBuilder {
        params.imageMaxSideSize = imageMaxSideSize
        return this
    }

}