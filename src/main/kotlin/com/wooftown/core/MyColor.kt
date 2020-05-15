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
    fun opposite() = when(this){
        WHITE -> BLACK
        BLACK -> WHITE
    }

    override fun toString() = name.toLowerCase()
}