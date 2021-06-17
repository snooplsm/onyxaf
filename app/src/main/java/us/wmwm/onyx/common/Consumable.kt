package us.wmwm.onyx.common

class Consumable<T>(private val t: T) {
    var consumed: Boolean = false

    fun consume(): T? {
        if (consumed) {
            return null
        }
        consumed = true
        return t
    }

    val peek: T = t
}