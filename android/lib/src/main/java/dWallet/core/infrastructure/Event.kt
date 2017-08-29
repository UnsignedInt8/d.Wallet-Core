package dwallet.core.infrastructure

/**
 * Created by unsignedint8 on 8/18/17.
 */


open class Event {

    private val observers = mutableMapOf<String, MutableList<EventCallback>>()

    protected fun register(event: String, callback: EventCallback) {
        var list = observers[event]
        if (list == null) {
            list = mutableListOf()
            observers[event] = list
        }

        list.add(callback)
    }

    protected fun trigger(event: String, sender: Any, params: Any) = observers[event]?.forEach { it.invoke(sender, params) }
}