package io.github.unsignedint8.dwallet_core.extensions

/**
 * Created by unsignedint8 on 8/19/17.
 */

inline fun <S, T> List<T>.reduce(seed: S, operation: (item: T, acc: S) -> S): S {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return seed
    var accumulator: S = seed
    while (iterator.hasNext()) {
        accumulator = operation(iterator.next(), accumulator)
    }
    return accumulator
}