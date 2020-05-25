package com.wooftown.core.pieces

import com.wooftown.core.ChessBoard
import com.wooftown.core.MyColor

/**
 * Knight
 * Extends Piece
 * @param color - color of piece
 * @param board - pointer of desk
 */
class Knight(color: MyColor, board: ChessBoard) : Piece(color, board) {

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    override fun getPossibleMoves(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val board = this.getBoard()
        for ((directionX, directionY) in listOf(2 to 1, 2 to -1, 1 to 2, 1 to -2, -1 to 2, -1 to -2, -2 to 1, -2 to -1)) {
            val newX = x + directionX
            val newY = y + directionY
            if (newX in 0 until board.getSize() && newY in 0 until board.getSize() && isOpposite(board[newX, newY])) {
                result.add(Pair(newX, newY))
            }
        }
        return result
    }


}

