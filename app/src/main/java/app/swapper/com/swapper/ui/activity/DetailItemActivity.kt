package app.swapper.com.swapper.ui.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.ImageView
import app.swapper.com.swapper.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail_item.*
import kotlinx.android.synthetic.main.content_detail_item.*
import com.synnapps.carouselview.CarouselView
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.synnapps.carouselview.ImageListener
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop







class DetailItemActivity : BaseActivity() {
    override fun onPermissionGranted(grantedPermissions: Collection<String>) {

    }

    override fun onPermissionDenied(deniedPermissions: Collection<String>) {

    }

    companion object {
        const val itemId = "itemId"
    }

    var sampleNetworkImageURLs = arrayOf("https://placeholdit.imgix.net/~text?txtsize=15&txt=image1&txt=350%C3%97150&w=350&h=150", "https://placeholdit.imgix.net/~text?txtsize=15&txt=image2&txt=350%C3%97150&w=350&h=150", "https://placeholdit.imgix.net/~text?txtsize=15&txt=image3&txt=350%C3%97150&w=350&h=150", "https://placeholdit.imgix.net/~text?txtsize=15&txt=image4&txt=350%C3%97150&w=350&h=150", "https://placeholdit.imgix.net/~text?txtsize=15&txt=image5&txt=350%C3%97150&w=350&h=150")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        carouselView.pageCount = 5
        carouselView.setImageListener(imageListener)
    }

    var imageListener: ImageListener = ImageListener { position, imageView ->
        Glide.with(applicationContext).load(sampleNetworkImageURLs[position]).into(imageView)

        //imageView.setImageResource(sampleImages[position]);
    }

}
