package com.wooftown.core.pieces

import com.wooftown.core.BOARD_SIZE

/**
 * Pawn
 * Extends Piece
 * @param color - color of piece
 */
class Pawn(color: PieceColor) : Piece(color) {
    /**
     * Need for En Passant
     */
    var moveDouble = false

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    override fun getPossibleMovies(x: Int, y: Int): List<Pair<Int, Int>> {
        val direction = if (color == PieceColor.BLACK) {
            1
        } else {
            -1
        }
        val result = mutableListOf(x to y)
        val board = this.getBoard()!!
        if (x == 1 && color == PieceColor.BLACK) {
            if (board[x + 2, y] == null)
                result.add(x + 2 to y)
        }
        if (x == 6 && color == PieceColor.WHITE) {
            if (board[x - 2, y] == null)
                result.add(x - 2 to y)
        }
        if (x + direction in 0..7 && board[x + direction, y] == null) {
            result.add(Pair(x + direction, y))
        }

        if (y + 1 in 0 until BOARD_SIZE && board[x, y + 1] is Pawn && (board[x, y + 1] as Pawn).moveDouble && isOpposite(board[x, y + 1])) {
            if (board[x + direction, y + 1] == null) {
                result.add(x + direction to y + 1)
            }
        }
        if (y - 1 in 0 until BOARD_SIZE && board[x, y - 1] is Pawn && (board[x, y - 1] as Pawn).moveDouble && isOpposite(board[x, y - 1])) {
            if (board[x + direction, y - 1] == null) {
                result.add(x + direction to y - 1)
            }
        }
        if (x + direction in 0 until BOARD_SIZE && y + 1 in 0 until BOARD_SIZE && board[x + direction, y + 1] is Piece && isOpposite(board[x + direction, y + 1])) {
            result.add(Pair(x + direction, y + 1))
        }
        if (x + direction in 0 until BOARD_SIZE && y - 1 in 0 until BOARD_SIZE && board[x + direction, y - 1] is Piece && isOpposite(board[x + direction, y - 1])) {
            result.add(Pair(x + direction, y - 1))
        }
        return result
    }

    /**
     * @return string with color and name of piece in format: color_piece
     * use in view for take images from resources
     */
    override fun toString(): String = if (this.color == PieceColor.WHITE) {
        "white_pawn"
    } else {
        "black_pawn"
    }
}

