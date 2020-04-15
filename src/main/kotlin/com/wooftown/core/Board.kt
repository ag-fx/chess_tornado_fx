package com.wooftown.core

/**
 * Interface for more comfort using gaming desk
 */

interface Board<E> {
    /**
     * @param x - x cords
     * @param y - y cords
     * @return value
     * get value
     */
    operator fun get(x: Int, y: Int): E

    /**
     * @param x - x cords
     * @param y - y cords
     * @param value
     * set value
     */
    operator fun set(x: Int, y: Int, value: E)
}