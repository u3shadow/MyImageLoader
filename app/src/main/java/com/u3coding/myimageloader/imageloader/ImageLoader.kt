package com.u3coding.myimageloader.imageloader

import android.content.Context

object ImageLoader{
    fun withContext(context: Context): RequestBuilder {
        return RequestBuilder().withContext(context)
    }
}