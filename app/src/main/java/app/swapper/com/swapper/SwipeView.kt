package app.swapper.com.swapper

import android.content.Context
import android.util.AttributeSet
import com.mindorks.placeholderview.SwipePlaceHolderView

/**
 * Created by Deividas on 2018-04-07.
 */
class SwipeView : SwipePlaceHolderView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context?, attrs : AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}