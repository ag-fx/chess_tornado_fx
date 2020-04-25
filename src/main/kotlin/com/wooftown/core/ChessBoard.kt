package com.wooftown.core

import com.wooftown.core.pieces.King
import com.wooftown.core.pieces.Piece
import com.wooftown.core.pieces.PieceColor

/**
 * Chess desk
 */
open class ChessBoard{
    /**
     * Out gaming desk
     */
    private val data = List(BOARD_SIZE) { MutableList<Piece?>(BOARD_SIZE) { null } }

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
        require(x in 0 until BOARD_SIZE && y in 0 until BOARD_SIZE)
        data[x][y] = value
        if (value is Piece) {
            value.setBoard(this)

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
        for (i in 0 until BOARD_SIZE) {
            for (j in 0 until BOARD_SIZE) {
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
        for (i in 0 until BOARD_SIZE) {
            for (j in 0 until BOARD_SIZE) {
                if (data[i][j] != null && data[i][j]!!.color != color) {
                    if (king in data[i][j]!!.getPossibleMovies(i, j)) {
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
    fun getPossibleMovies(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf(x to y)
        for (move in data[x][y]!!.getPossibleMovies(x, y).toMutableList().drop(1)) {
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
        for (i in 0 until BOARD_SIZE) {
            for (j in 0 until BOARD_SIZE) {
                if (data[i][j] != null && data[i][j]!!.color == color) {
                    if (getPossibleMovies(i, j).size > 1) {
                        return false
                    }
                }
            }
        }
        return true
    }
}
