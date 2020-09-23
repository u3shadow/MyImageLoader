package com.u3coding.myimageloader.imageloader

import android.graphics.*

object BitmapRounder{
     fun getRoundedCornerBitmap(bitmap: Bitmap, round: Float): Bitmap? {
        return try {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            val rectF = RectF(
                Rect(
                    0, 0, bitmap.width,
                    bitmap.height
                )
            )
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.BLACK
            canvas.drawRoundRect(rectF, round, round, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val src = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            canvas.drawBitmap(bitmap, src, rect, paint)
            output
        } catch (e: Exception) {
            bitmap
        }
    }
}