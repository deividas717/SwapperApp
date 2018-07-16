package app.swapper.com.swapper.swipableCard

import rx.Observable
import rx.subjects.PublishSubject

object RxBus {

    private val bus = PublishSubject.create<Any>()

    fun send(o: Any) {
        bus.onNext(o)
    }

    fun toObserverable(): Observable<Any> {
        return bus
    }

    fun hasObservers(): Boolean {
        return bus.hasObservers()
    }
}
