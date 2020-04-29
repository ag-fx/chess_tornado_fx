package com.wooftown.core.pieces

import com.wooftown.core.ChessBoard
import com.wooftown.core.PieceColor

/**
 * abstract description of piece
 * @param color - color of piece
 */
abstract class Piece(val color: PieceColor) {

    private var board: ChessBoard? = null

    /**
     * set pointer of desk
     * @param board - desk
     */
    fun setBoard(board: ChessBoard) {
        this.board = board
    }

    /**
     * @return pointer of desk
     */
    fun getBoard() = board

    /**
     * @param - other piece
     * @return - true if color of this != color of other piece
     */
    fun isOpposite(other: Piece?) = (other?.color ?: false) != this.color

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    abstract fun getPossibleMovies(x: Int, y: Int): List<Pair<Int, Int>>

    // в этом хаш-коде буду равны одинаковые фигуры одного цвета
    override fun hashCode(): Int = toString().hashCode()

    // idea generated code
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Piece

        if (color != other.color) return false
        if (board != other.board) return false

        return true
    }

}