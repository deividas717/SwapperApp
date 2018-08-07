package app.swapper.com.swapper.swipableCard

import android.content.Context
import android.util.AttributeSet
import android.util.EventLog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import app.swapper.com.swapper.events.OnCardClickedEvent
import app.swapper.com.swapper.utils.DisplayUtils
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription

class TinderStackLayout : FrameLayout {
    val publishSubject = PublishSubject.create<Int>()
    private var compositeSubscription: CompositeSubscription? = null
    private var screenWidth: Int = 0
    private var yMultiplier: Int = 0

    private var canClick = true

    // endregion

    // region Constructors
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }
    // endregion

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        publishSubject?.onNext(childCount)
    }

    override fun removeView(view: View) {
        super.removeView(view)
        publishSubject?.onNext(childCount)
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeSubscription?.unsubscribe()
    }

    // region Helper Methods
    private fun init() {
        clipChildren = false

        screenWidth = DisplayUtils.getScreenWidth(context)
        yMultiplier = DisplayUtils.dp2px(context, 8)

        compositeSubscription = CompositeSubscription()

        setUpRxBusSubscription()

        setOnClickListener {
            Log.d("SDIOASDSD", "sadfhkjshkdfdf")
        }
    }

    private fun setUpRxBusSubscription() {
        val rxBusSubscription = RxBus.toObserverable()
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(Action1<Any> { event ->
                    if (event == null) {
                        return@Action1
                    }

                    if (event is TopCardMovedEvent) {
                        val posX = event.posX
                        canClick = posX == 0f

                        val childCount = childCount
                        for (i in childCount - 2 downTo 0) {
                            val tinderCardView = getChildAt(i) as TinderCardView

                            if (Math.abs(posX) == screenWidth.toFloat()) {
                                val scaleValue = 1 - (childCount - 2 - i) / 50.0f

                                tinderCardView.animate()
                                        .x(0.toFloat())
                                        .y((childCount - 2 - i) * yMultiplier.toFloat())
                                        .scaleX(scaleValue)
                                        .rotation(0.toFloat())
                                        .setInterpolator(AnticipateOvershootInterpolator()).duration = DURATION.toLong()
                            }
                        }
                    } else if (event is TopCardMoveUpEvent) {
                        val posX = event.posX

                        if (Math.abs(posX) <= 15) {
                            EventBus.getDefault().post(OnCardClickedEvent(getTopCard().id))
                        }
                    }
                })

        compositeSubscription?.add(rxBusSubscription)
    }

    fun addCard(tc: TinderCardView) {
        val layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        addView(tc, 0, layoutParams)

        val scaleValue = 1 - childCount / 50.0f

        tc.animate()
                .x(0f)
                .y((childCount * yMultiplier).toFloat())
                .scaleX(scaleValue)
                .setInterpolator(AnticipateOvershootInterpolator()).duration = DURATION.toLong()
    }

    private fun getTopCard(): TinderCardView {
        return getChildAt(childCount - 1) as TinderCardView
    }

    fun enableSwipeForCard(enableSwipeToRight: Boolean) {
        getTopCard().canBeSwipedToRight = enableSwipeToRight
    }

    companion object {
        private const val DURATION = 300
    }
}