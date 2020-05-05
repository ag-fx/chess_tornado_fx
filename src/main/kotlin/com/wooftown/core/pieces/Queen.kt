package com.wooftown.core.pieces

import com.wooftown.core.PieceColor

/**
 * Queen
 * Extends Piece
 * @param color - color of piece
 */
class Queen(color: PieceColor) : Piece(color) {

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    override fun getPossibleMovies(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val board = this.getBoard()!!
        for ((directionX, directionY) in listOf(1 to 1, 1 to 0, 1 to -1, 0 to -1, -1 to -1, -1 to 0, -1 to 1, 0 to 1)) {
            var newX = x + directionX
            var newY = y + directionY
            while (newX in 0 until board.getSize() && newY in 0 until board.getSize() && isOpposite((board[newX, newY]))) {
                result.add(Pair(newX, newY))
                if ((board[newX, newY]) is Piece) {
                    break
                }
                newX += directionX
                newY += directionY
            }
        }
        return result
    }

}