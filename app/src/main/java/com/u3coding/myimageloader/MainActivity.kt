package com.u3coding.myimageloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.u3coding.myimageloader.imageloader.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImageLoader.withContext(this)
            .placeholder(R.drawable.holder)
            .load("https://www.baidu.com/img/flexible/logo/pc/result.png")
            .useCache(true)
            .into(image)

        ImageLoader.withContext(this)
            .placeholder(R.drawable.holder)
            .load("http://www.u3coding.com/wp-content/uploads/2016/02/QQ%E6%88%AA%E5%9B%BE20160201162838.png")
            .useCache(true)
            .round(10f)
            .into(image2)
    }
}