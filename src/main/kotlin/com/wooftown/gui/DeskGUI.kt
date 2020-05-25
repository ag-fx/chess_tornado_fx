package com.wooftown.gui

import com.wooftown.core.ChessBoard
import com.wooftown.core.MyColor
import com.wooftown.core.pieces.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.math.abs

/**
 * Extended desk class for interaction with GUI
 * @param cells - List of cells for changing their
 * @param images - List of images for changing their
 */
class DeskGUI(private val cells: List<List<Rectangle>>,
              private val images: List<List<ImageView>>, size: Int) : ChessBoard(size) {
    /**
     * Pieces style
     */
    private var style = PieceStyles.CLASSIC

    /**
     * Set style
     */
    fun setStyle(newStyle: PieceStyles) {
        style = newStyle
        redrawAll()
    }

    /**
     * Change image for new style
     */
    private fun redrawAll() {
        for (row in 0 until getSize()) {
            for (column in 0 until getSize()) {
                if (this[row, column] is Piece) {
                    setImage(row, column, Image("file:src\\main\\resources\\$style\\${this[row, column]}.png"))
                }
            }
        }
    }


    /**
     * Change image on this cell
     * @param row - row of cell
     * @param column - column of cell
     * @param image - new image
     */
    fun setImage(row: Int, column: Int, image: Image?) {
        images[row][column].image = image
    }

    /**
     * Get image of this cell
     * @param row - row of cell
     * @param column - column of cell
     * @return image?
     */
    fun getImage(row: Int, column: Int): Image? = images[row][column].image

    /**
     * Change color of this cell
     * @param row - row of cell
     * @param column - column of cell
     * @param color - new color
     */
    fun setCellColor(row: Int, column: Int, color: Color) {
        cells[row][column].fill = color
    }

    /**
     * Set up piece on super.desk and add image of cell
     * @param row - row of cell
     * @param column - column of cell
     * @param piece - piece
     */
    fun spawnPiece(piece: Piece, row: Int, column: Int) {
        this[row, column] = piece
        setImage(row, column, Image("file:src\\main\\resources\\$style\\${piece}.png"))
    }

    /**
     * Delete piece
     * @param row - row of cell
     * @param column - column of cell
     */
    private fun despairPiece(row: Int, column: Int) {
        this[row, column] = null
        setImage(row, column, null)

    }

    /**
     * Moving piece
     * Dealt with many cases
     * @param row - old row of cell
     * @param column - old column of cell
     * @param newRow - new row of cell
     * @param newColumn - new column of cell
     */
    fun movePiece(row: Int, column: Int, newRow: Int, newColumn: Int) {
        // castling
        if (this[row, column] is King) {
            val castleX = if (this[row, column]!!.color == MyColor.WHITE) {
                getSize() - 1
            } else {
                0
            }
            if (column - newColumn == 2) {
                movePiece(castleX, 0, castleX, 3)

            }
            if (newColumn - column == 2) {
                movePiece(castleX, 7, castleX, 5)
            }
        }
        val deleted = this[newRow, newColumn]
        this[newRow, newColumn] = this[row, column]
        this[row, column] = null
        setImage(newRow, newColumn, getImage(row, column))
        setImage(row, column, null)
        // changing to queen

        if (this[newRow, newColumn] is King) {
            (this[newRow, newColumn] as King).walked = true
        }

        if (this[newRow, newColumn] is Rook) {
            (this[newRow, newColumn] as Rook).walked = true
        }


        //el passant
        if (this[newRow, newColumn] is Pawn) {

            (this[newRow, newColumn] as Pawn).moveDouble = abs(newRow - row) == 2

            if (abs(column - newColumn) == 1 && deleted == null) {
                if (this[newRow, newColumn]!!.color == MyColor.WHITE) {
                    despairPiece(newRow + 1, newColumn)
                } else {
                    despairPiece(newRow - 1, newColumn)
                }
            }
            if (this[newRow, newColumn]!!.color == MyColor.BLACK && newRow == 7) {
                spawnPiece(Queen(MyColor.BLACK, this), newRow, newColumn)
            } else {
                if (this[newRow, newColumn]!!.color == MyColor.WHITE && newRow == 0) {
                    spawnPiece(Queen(MyColor.WHITE, this), newRow, newColumn)
                }
            }
        }
        setWalked(newRow, newColumn)
    }

    /**
     * Clear super.desk and our gui desk
     */
    override fun clear() {
        super.clear()
        images.forEach { list -> list.forEach { it.image = null } }
    }

}