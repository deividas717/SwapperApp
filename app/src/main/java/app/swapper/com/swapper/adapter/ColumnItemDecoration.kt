package app.swapper.com.swapper.adapter

/**
 * Created by Deividas on 2018-05-02.
 */
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View


/**
 * An ItemDecoration which will apply even horizontal padding to the right and left edges
 * of items in a vertical grid, while keeping the size of each item the same.
 *
 * It even works when there are different types in each row, as long as each
 * row only contains one view type and each view type always spans the same number of columns.
 *
 */
class ColumnItemDecoration : RecyclerView.ItemDecoration() {

    // Horizontal padding
    private val padding: Int = 0

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val gridLayoutManager = parent.layoutManager as GridLayoutManager
        val position = parent.getChildViewHolder(view).adapterPosition
        val spanSize = gridLayoutManager.spanSizeLookup.getSpanSize(position).toFloat()
        val totalSpanSize = gridLayoutManager.spanCount.toFloat()

        val n = totalSpanSize / spanSize // num columns
        val c = layoutParams.spanIndex / spanSize // column index

        val leftPadding = padding * ((n - c) / n)
        val rightPadding = padding * ((c + 1) / n)

        outRect.left = leftPadding.toInt()
        outRect.right = rightPadding.toInt()
    }
}