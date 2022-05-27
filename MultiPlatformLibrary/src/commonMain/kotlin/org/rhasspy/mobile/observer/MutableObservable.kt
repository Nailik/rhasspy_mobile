package org.rhasspy.mobile.observer

class MutableObservable<T>(initialValue: T) : Observable<T>(initialValue) {

    override var value: T = initialValue
        get() = super.value
        set(value) {
            field = value
            //invoke all observers
            observers.forEach { observer ->
                observer.invoke(value)
            }
        }

    fun readonly(): Observable<T> = this
}