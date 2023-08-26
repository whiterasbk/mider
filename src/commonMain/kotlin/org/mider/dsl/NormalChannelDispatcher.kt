package org.mider.dsl

interface DispatcherControlled

class NormalChannelDispatcher {

//    private val availableChannel = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14)
    private val used = mutableMapOf<Int, DispatcherControlled?>(
        1 to null,
        2 to null,
        3 to null,
        4 to null,
        5 to null,
        6 to null,
        8 to null,
        9 to null,
        11 to null,
        12 to null,
        13 to null,
        14 to null,
        15 to null
    )

    private val useChannel0 = mutableSetOf<DispatcherControlled>()

    private val controlList = mutableSetOf<DispatcherControlled>()


    /**
     * 如果 1~15 (排除 10) 号通道中有 track 使用了则返回这个 track 用的通道
     */
    fun getChannel(dc: DispatcherControlled): Int {
        return if (used.containsValue(dc)) {
            used.filter { it.value == dc }.keys.first()
        } else {
            val filter = used.filter { it.value == null }
            if (filter.isEmpty()) {
                useChannel0 += dc
                0
            } else {
                val first = filter.keys.first()
                used[first] = dc
                first
            }
        }
    }

    fun mount(dc: DispatcherControlled) {
        controlList += dc
    }
}
