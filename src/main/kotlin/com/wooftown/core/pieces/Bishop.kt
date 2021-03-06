package com.wooftown.core.pieces

import com.wooftown.core.ChessBoard
import com.wooftown.core.MyColor


/**
 * Bishop
 * Extends Piece
 * @param color - color of piece
 * @param board - pointer of desk
 */
class Bishop(color: MyColor, board: ChessBoard) : Piece(color,board) {

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    override fun getPossibleMoves(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val board = this.getBoard()
        for ((directionX, directionY) in listOf(Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1))) {
            var newX = x + directionX
            var newY = y + directionY
            while (newX in 0 until board.getSize() && newY in 0 until board.getSize() && isOpposite(board[newX, newY])) {
                result.add(Pair(newX, newY))
                if (board[newX, newY] is Piece) {
                    break
                }
                newX += directionX
                newY += directionY
            }
        }
        return result
    }

}
