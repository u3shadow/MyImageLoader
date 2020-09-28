package com.u3coding.myimageloader.imageloader

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable


class MyAnimationDrawable(private val bitmap: Bitmap, private val placeholder: Bitmap) : Drawable(),Animatable {
    private var mValueAnimator = ValueAnimator()
    var placeholderAlpha = 250
    var paint:Paint
    var isFinish = false
    init {
        mValueAnimator = ObjectAnimator.ofInt(this,"placeholderAlpha",0)
        mValueAnimator.duration = 1200
        mValueAnimator.startDelay = 1000
        paint = Paint()
        mValueAnimator.addUpdateListener { // 监听属性动画并进行重绘
            invalidateSelf()
        }
    }
    override fun draw(canvas: Canvas){
        if (!isFinish) {
            val rectF = RectF(
                0f,
                0f,
                bounds.width().toFloat(),
                bounds.height().toFloat()
            ) //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高
            paint.reset()
            canvas.drawBitmap(bitmap, null, rectF, paint)
            paint.alpha = placeholderAlpha
            canvas.drawBitmap(placeholder, null, rectF, paint)
            paint.reset()
        }
        if (placeholderAlpha == 0)isFinish = true
    }

    override fun setAlpha(p0: Int) {
    }

    @SuppressLint("WrongConstant")
    override fun getOpacity(): Int {
        return  1
    }

    override fun setColorFilter(p0: ColorFilter?) {
    }

    override fun isRunning(): Boolean {
        return mValueAnimator.isRunning
    }

    override fun start() {
        mValueAnimator.start()
    }

    override fun stop() {
    }

}