package com.wooftown.gui

/**
 * Possible styles of piece
 */
enum class PieceStyles {
    CLASSIC, BERLIN, METRO, CHESS24;

    override fun toString(): String {
        return this.name.toLowerCase()
    }
}