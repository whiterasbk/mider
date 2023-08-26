package org.mider.impl

open class Stack<T> : Iterable<T> {
    private val insideList: MutableList<T> = mutableListOf()

    fun push(element: T) {
        insideList += element
    }

    fun pop(): T {
        return insideList.removeLast()
    }

    fun popTillEmpty(): List<T> {
        val ret = mutableListOf<T>()
        for (e in 0 ..< insideList.size)
            ret += pop()
        return ret
    }

    fun popTillEmptyFromStart(): List<T> {
        val ret = mutableListOf<T>()
        for (e in 0 ..< insideList.size)
            ret += insideList.removeFirst()
        return ret
    }

    fun top(): T {
        return insideList.last()
    }

    fun isNotEmpty(): Boolean {
        return insideList.isNotEmpty()
    }

    fun clear() {
        insideList.clear()
    }

    fun getPrototype() = insideList

    override fun iterator(): Iterator<T> {
        return insideList.iterator()
    }
}