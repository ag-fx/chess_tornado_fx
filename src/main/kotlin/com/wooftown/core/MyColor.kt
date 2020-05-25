package com.wooftown.core

/**
 * Colors of pieces
 */
enum class MyColor {
    /**
     * White piece
     */
    WHITE,

    /**
     * Black piece
     */
    BLACK;

    /**
     * @return opposite color
     */
    fun opposite() = when (this) {
        WHITE -> BLACK
        BLACK -> WHITE
    }

    /**
     * Also needs for taking png of pieces
     */
    override fun toString() = name.toLowerCase()
}