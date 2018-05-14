package app.swapper.com.swapper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import app.swapper.com.swapper.R

class MatchDialog: DialogFragment() {

    override fun onResume() {
        super.onResume()

        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val width = calculateSize(display.width, 90)
        val height = calculateSize(display.height, 70)
        dialog.window?.setLayout(width, height)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_color)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.match_dialog, container, false)
        return view
    }

    private fun calculateSize(size: Int, percent: Int) = (size * percent) / 100
}