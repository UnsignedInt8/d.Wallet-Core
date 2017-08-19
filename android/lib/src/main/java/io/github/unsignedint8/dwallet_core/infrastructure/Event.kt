package io.github.unsignedint8.dwallet_core.infrastructure

/**
 * Created by unsignedint8 on 8/18/17.
 */

typealias Callback = (sender: Any, params: Any) -> Unit

open class Event {

    private val observers = mutableMapOf<String, MutableList<Callback>>()

    protected fun register(event: String, callback: Callback) {
        var list = observers[event]
        if (list == null) {
            list = mutableListOf()
            observers[event] = list
        }

        list.add(callback)
    }

    protected fun trigger(event: String, sender: Any, vararg params: Any) {
        observers[event]?.forEach {
            it.invoke(sender, params)
        }
    }
}