package com.wooftown.core

import com.wooftown.core.pieces.King
import com.wooftown.core.pieces.Piece

/**
 * Chess desk
 */
open class ChessBoard(private val size: Int) {

    fun getSize() = size

    /**
     * Out gaming desk
     */
    private val data = List(size) { MutableList<Piece?>(size) { null } }

    /**
     * Cords of white king , uses for castles
     */
    private var whiteKing = 7 to 4

    /**
     * Cords of black king , uses for castles
     */
    private var blackKing = 0 to 4

    /**
     * @param color - color of king which must to find
     * @return cords of king
     */
    private fun getKing(color: PieceColor): Pair<Int, Int> =
            if (color == PieceColor.WHITE) {
                whiteKing
            } else {
                blackKing
            }

    /**
     * get piece on x y
     * @param x - x cords of desk
     * @param y - y cords of desk
     * @return desk[x][y] piece
     */
    operator fun get(x: Int, y: Int): Piece? = data[x][y]

    /**
     * set piece on x y
     * @param x - x cords of desk
     * @param y - y cords of desk
     * @param value
     */
    operator fun set(x: Int, y: Int, value: Piece?) {
        require(x in 0 until size && y in 0 until size)
        data[x][y] = value
        if (value is Piece) {
            if (value is King) {
                if (value.color == PieceColor.WHITE) {
                    whiteKing = x to y
                } else {
                    blackKing = x to y
                }
            }
        }
    }

    /**
     * Clear desk
     */
    open fun clear() {
        for (i in 0 until size) {
            for (j in 0 until size) {
                data[i][j] = null
            }
        }
    }

    /**
     * @param color - color of checking player
     * @return boolean - true os castled
     */
    private fun isCheck(color: PieceColor): Boolean {
        val king = getKing(color)
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (data[i][j] is Piece && data[i][j]!!.color != color) {
                    if (king in data[i][j]!!.getPossibleMoves(i, j)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies WITH finding for check
     */
    fun getPossibleMoves(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (move in (data[x][y] ?: return result).getPossibleMoves(x, y).toMutableList()) {
            val piece = this[x, y]
            val otherPiece = this[move.first, move.second]
            this[move.first, move.second] = piece
            this[x, y] = null
            if (!isCheck(piece!!.color)) {
                result.add(move)
            }
            this[x, y] = piece
            this[move.first, move.second] = otherPiece
        }
        return result
    }

    /**
     * check for looser in game
     * @return color of looser
     */
    fun isLooser(color: PieceColor): Boolean {
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (data[i][j] != null && data[i][j]!!.color == color) {
                    if (getPossibleMoves(i, j).isNotEmpty()) {
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun hashCode(): Int = data.hashCode()

    override fun equals(other: Any?): Boolean = other is ChessBoard && other.data == this.data
}