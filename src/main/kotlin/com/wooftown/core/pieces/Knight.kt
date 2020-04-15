package com.wooftown.core.pieces

/**
 * Knight
 * Extends Piece
 * @param color - color of piece
 */
class Knight(color: PieceColor) : Piece(color) {

    /**
     * @param x - x cords of piece
     * @param y - y cords of piece
     * @return list of possible movies without finding for check
     */
    override fun getPossibleMovies(x: Int, y: Int): List<Pair<Int, Int>> {
        val result = mutableListOf(x to y)
        val board = this.getBoard()!!
        for ((directionX, directionY) in listOf(2 to 1, 2 to -1, 1 to 2, 1 to -2, -1 to 2, -1 to -2, -2 to 1, -2 to -1)) {
            val newX = x + directionX
            val newY = y + directionY
            if (newX in 0..7 && newY in 0..7 && isOpposite(board[newX, newY])) {
                result.add(Pair(newX, newY))
            }
        }
        return result
    }

    /**
     * @return string with color and name of piece in format: color_piece
     * use in view for take images from resources
     */
    override fun toString(): String = if (this.color == PieceColor.WHITE) {
        "white_knight"
    } else {
        "black_knight"
    }
}

