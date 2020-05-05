package com.wooftown.core

/**
 * Colors of pieces
 */
enum class PieceColor {
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
    fun opposite() = if (this == WHITE) {
        BLACK
    } else {
        WHITE
    }

    override fun toString() = name.toLowerCase()
}