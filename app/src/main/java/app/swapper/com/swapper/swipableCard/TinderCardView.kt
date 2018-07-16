package app.swapper.com.swapper.swipableCard

import android.animation.Animator
import android.content.Context
import android.databinding.DataBindingUtil
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import app.swapper.com.swapper.R
import app.swapper.com.swapper.databinding.CardLayoutBinding
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.events.OnCardDismissedEvent
import app.swapper.com.swapper.utils.DisplayUtils
import org.greenrobot.eventbus.EventBus

class TinderCardView : FrameLayout, View.OnTouchListener {

    // region Member Variables
    private var oldX: Float = 0.toFloat()
    private var oldY: Float = 0.toFloat()
    private var newX: Float = 0.toFloat()
    private var newY: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()
    private var rightBoundary: Float = 0.toFloat()
    private var leftBoundary: Float = 0.toFloat()
    private var screenWidth: Int = 0
    private var padding: Int = 0

    constructor(context: Context, item: Item) : super(context) {
        init(context, null, item)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val tinderStackLayout = view.parent as TinderStackLayout
        val topCard = tinderStackLayout.getChildAt(tinderStackLayout.childCount - 1) as TinderCardView
        if (topCard == view) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldX = motionEvent.x
                    oldY = motionEvent.y

                    // cancel any current animations
                    view.clearAnimation()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    when {
                        isCardBeyondLeftBoundary(view) -> {
                            RxBus.send(TopCardMovedEvent(-screenWidth.toFloat()))
                            dismissCard(view, -(screenWidth * 2))
                        }
                        isCardBeyondRightBoundary(view) -> {
                            RxBus.send(TopCardMovedEvent(screenWidth.toFloat()))
                            dismissCard(view, screenWidth * 2)
                        }
                        else -> {
                            RxBus.send(TopCardMovedEvent(0.toFloat()))
                            resetCard(view)
                        }
                    }
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    newX = motionEvent.x
                    newY = motionEvent.y

                    dX = newX - oldX
                    dY = newY - oldY

                    val posX = view.getX() + dX

                    RxBus.send(TopCardMovedEvent(posX))

                    // Set new position
                    view.setX(view.getX() + dX)
                    view.setY(view.getY() + dY)

                    setCardRotation(view, view.getX())

                    updateAlphaOfBadges(posX)
                    return true
                }
                else -> return super.onTouchEvent(motionEvent)
            }
        }
        return super.onTouchEvent(motionEvent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnTouchListener(null)
    }

    private fun init(context: Context, attrs: AttributeSet?, item: Item? = null) {
        if (!isInEditMode) {
            val dataBinding = DataBindingUtil.inflate<CardLayoutBinding>(LayoutInflater.from(context), R.layout.card_layout, this, true)
            dataBinding.item = item
            //likeTextView?.rotation = -BADGE_ROTATION_DEGREES
            //nopeTextView?.rotation = BADGE_ROTATION_DEGREES

            screenWidth = DisplayUtils.getScreenWidth(context)
            leftBoundary = screenWidth * (1.0f / 6.0f) // Left 1/6 of screen
            rightBoundary = screenWidth * (5.0f / 6.0f) // Right 1/6 of screen
            padding = DisplayUtils.dp2px(context, 16)

            setOnTouchListener(this)
        }
    }

    // Check if card's middle is beyond the left boundary
    private fun isCardBeyondLeftBoundary(view: View): Boolean {
        return view.x + view.width / 2 < leftBoundary
    }

    // Check if card's middle is beyond the right boundary
    private fun isCardBeyondRightBoundary(view: View): Boolean {
        return view.x + view.width / 2 > rightBoundary
    }

    private fun dismissCard(view: View, xPos: Int) {
        view.animate()
                .x(xPos.toFloat())
                .y(0f)
                .setInterpolator(AccelerateInterpolator())
                .setDuration(DURATION.toLong())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {

                    }

                    override fun onAnimationEnd(animator: Animator) {
                        val viewGroup = view.parent as ViewGroup
                        viewGroup.removeView(view)
                        EventBus.getDefault().post(OnCardDismissedEvent(viewGroup.childCount))
                    }

                    override fun onAnimationCancel(animator: Animator) {

                    }

                    override fun onAnimationRepeat(animator: Animator) {

                    }
                })
    }

    private fun resetCard(view: View) {
        view.animate()
                .x(0f)
                .y(0f)
                .rotation(0f)
                .setInterpolator(OvershootInterpolator()).duration = DURATION.toLong()

        //likeTextView!!.alpha = 0f
        //nopeTextView!!.alpha = 0f
    }

    private fun setCardRotation(view: View, posX: Float) {
        val rotation = CARD_ROTATION_DEGREES * posX / screenWidth
        val halfCardHeight = view.height / 2
        if (oldY < halfCardHeight - 2 * padding) {
            view.rotation = rotation
        } else {
            view.rotation = -rotation
        }
    }

    // set alpha of like and nope badges
    private fun updateAlphaOfBadges(posX: Float) {
        val alpha = (posX - padding) / (screenWidth * 0.50f)
        //likeTextView!!.alpha = alpha
        //nopeTextView!!.alpha = -alpha
    }


    companion object {
        private const val CARD_ROTATION_DEGREES = 40.0f
        private const val BADGE_ROTATION_DEGREES = 15.0f
        private const val DURATION = 300
    }
}
