package org.rhasspy.mobile.observer

open class MutableObservable<T>(initialValue: T) : Observable<T>(initialValue) {

    override var value: T = initialValue
        set(value) {
            field = value
            //invoke all observers
            observers.forEach { observer ->
                observer.invoke(value)
            }
        }

    fun readOnly(): Observable<T> = this
}