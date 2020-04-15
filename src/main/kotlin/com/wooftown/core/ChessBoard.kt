package com.wooftown.core

import com.wooftown.core.pieces.King
import com.wooftown.core.pieces.Piece
import com.wooftown.core.pieces.PieceColor

/**
 * Chess desk
 */
class ChessBoard : Board<Piece?> {
    /**
     * Out gaming desk
     */
    private val data = List(8) { MutableList<Piece?>(8) { null } }

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
    override fun get(x: Int, y: Int): Piece? = data[x][y]

    /**
     * set piece on x y
     * @param x - x cords of desk
     * @param y - y cords of desk
     * @param value
     */
    override fun set(x: Int, y: Int, value: Piece?) {
        require(x in 0..7 && y in 0..7)
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
    fun clear() {
        for (i in 0..7) {
            for (j in 0..7) {
                data[i][j] = null
            }
        }
    }

    /**
     * @param color - color of checking player
     * @return boolean - true os castled
     */
    private fun kingUnderAttack(color: PieceColor): Boolean {
        val king = getKing(color)
        for (i in 0..7) {
            for (j in 0..7) {
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
            if (!kingUnderAttack(piece!!.color)) {
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
    fun checkLooser(): PieceColor? {
        val castledColor = if (kingUnderAttack(PieceColor.WHITE)) {
            PieceColor.WHITE
        } else {
            if (kingUnderAttack(PieceColor.BLACK)) {
                PieceColor.BLACK
            } else {
                return null
            }
        }
        for (i in 0..7) {
            for (j in 0..7) {
                if (data[i][j] != null && data[i][j]!!.color == castledColor) {
                    if (getPossibleMovies(i, j).size > 1) {
                        return null
                    }
                }
            }
        }
        return castledColor
    }
}
