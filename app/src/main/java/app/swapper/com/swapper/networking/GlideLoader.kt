package app.swapper.com.swapper.networking

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

/**
 * Created by Deividas on 2018-04-24.
 */

object GlideLoader {
    lateinit var accessToken : String

    fun load(context: Context?, view: ImageView, url: String) {
        context?.let {
            val glideUrl = GlideUrl(url, LazyHeaders.Builder().addHeader("Authorization", accessToken).build())
            Glide.with(it).load(glideUrl).into(view)
        }
    }
}