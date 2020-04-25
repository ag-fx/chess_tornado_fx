package com.wooftown.core.pieces

import com.wooftown.core.ChessBoard
import com.wooftown.core.PieceColor

/**
 * abstract description of piece
 * @param color - color of piece
 */
abstract class Piece(color: PieceColor) {
    val color: PieceColor = color

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

    override fun hashCode(): Int = this.toString().hashCode()
}