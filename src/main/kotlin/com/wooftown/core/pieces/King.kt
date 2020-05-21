package com.wooftown.core.pieces

import com.wooftown.core.ChessBoard
import com.wooftown.core.MyColor

/**
 * King
 * Extends Piece
 * @param color - color of piece
 * @param board - pointer of desk
 */
class King(color: MyColor, board: ChessBoard) : Piece(color,board){

    /**
     * Need for castling
     */
    var walked = false

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    override fun getPossibleMoves(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val board = this.getBoard()
        for ((directionX, directionY) in listOf(1 to 1, 1 to 0, 1 to -1, 0 to -1, -1 to -1, -1 to 0, -1 to 1, 0 to 1)) {
            val newX = x + directionX
            val newY = y + directionY
            if (newX in 0 until board.getSize() && newY in 0 until board.getSize() && isOpposite(board[newX, newY])) {
                result.add(Pair(newX, newY))
            }
        }

        return result + castling(x, y)
    }

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies with castling
     */
    private fun castling(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val board = this.getBoard()
        val canCastle = if (color == MyColor.WHITE) {
            x == board.getSize() -1 && y == 4 && !walked
        } else {
            x == 0 && y == 4 && !walked
        }
        if (canCastle) {
            if (board[x, 5] == null && board[x, 6] == null && board[x, 7] is Rook &&
                    !(board[x, 7] as Rook).walked) {
                result.add(x to 6)
            }
            if (board[x, 3] == null && board[x, 2] == null && board[x, 1] == null &&
                    board[x, 0] is Rook && !(board[x,0] as Rook).walked) {
                result.add(x to 2)
            }
        }

        return result
    }

}
