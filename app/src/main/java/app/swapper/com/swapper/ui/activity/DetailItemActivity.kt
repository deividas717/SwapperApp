package app.swapper.com.swapper.ui.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import app.swapper.com.swapper.R

import kotlinx.android.synthetic.main.activity_detail_item.*

class DetailItemActivity : BaseActivity() {

    companion object {
        const val itemId = "itemId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
