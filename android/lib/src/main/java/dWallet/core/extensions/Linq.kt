package dwallet.core.extensions

/**
 * Created by unsignedint8 on 8/19/17.
 */

inline fun <S, T> Iterable<T>.reduce(seed: S, operation: (item: T, acc: S) -> S): S {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return seed
    var accumulator: S = seed
    while (iterator.hasNext()) {
        accumulator = operation(iterator.next(), accumulator)
    }
    return accumulator
}


inline fun <S> ByteArray.reduce(seed: S, operation: (item: Byte, acc: S) -> S): S {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return seed
    var accumulator: S = seed
    while (iterator.hasNext()) {
        accumulator = operation(iterator.next(), accumulator)
    }
    return accumulator
}

inline fun <T> Iterable<T>.skip(n: Int): List<T> {
    require(n >= 0) { "Requested element count $n is less than zero." }
    if (n == 0) return this.toList()

    var count = 0
    val list = ArrayList<T>(n)
    for (item in this) {
        if (++count <= n)
            continue
        list.add(item)
    }
    return list
}

inline fun ByteArray.skip(n: Int): ByteArray {
    require(n >= 0)
    if (n == 0) return this

    var array = ArrayList<Byte>()
    this.filterIndexedTo(array) { count, item -> count + 1 > n }
    return array.toByteArray()
}

inline fun <T> Iterable<T>.sum(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}