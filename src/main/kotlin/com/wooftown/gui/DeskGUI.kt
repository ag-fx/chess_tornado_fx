package com.wooftown.gui

import com.wooftown.core.ChessBoard
import com.wooftown.core.pieces.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.math.abs

// расширенный класс доски, в которой реализованы ходы

class DeskGUI : ChessBoard() {
    private lateinit var cells : List<List<Rectangle>>
    private lateinit var images : List<List<ImageView>>

    fun setUp(cells : List<List<Rectangle>>,images : List<List<ImageView>>){
        this.cells = cells
        this.images = images
    }


    private fun setImage(row : Int, column : Int, image : Image?){
        images[row][column].image = image
    }

    private fun getImage(row : Int, column : Int) : Image? = images[row][column].image

    fun setCellColor(row: Int ,column: Int , color : Color){
        cells[row][column].fill = color
    }

    fun spawnPiece(piece: Piece, row: Int, column: Int){
        this[row,column]= piece
        setImage(row,column, Image("file:src\\main\\resources\\${piece}.png"))
    }

    private fun despairPiece(row:Int, column : Int){
        this[row,column] = null
        setImage(row,column, null)

    }

    fun movePiece(row: Int, column: Int, newRow: Int, newColumn: Int){

        if (this[row, column] is King) {
            val castleX = if (this[row, column]!!.color == PieceColor.WHITE) {
                7
            } else {
                0
            }
            if (column - newColumn == 2) {
                movePiece(castleX,0,castleX,3)

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


        if (this[newRow, newColumn] is Pawn) {

            (this[newRow, newColumn] as Pawn).moveDouble = abs(newRow - row) == 2

            if (abs(column - newColumn) == 1 && deleted == null) {
                if (this[newRow, newColumn]!!.color == PieceColor.WHITE) {
                    despairPiece(newRow + 1, newColumn)
                } else {
                    despairPiece(newRow - 1, newColumn)
                }
            }
            if (this[newRow, newColumn]!!.color == PieceColor.BLACK && newRow == 7) {
                spawnPiece(Queen(PieceColor.BLACK), newRow, newColumn)
            } else {
                if (this[newRow, newColumn]!!.color == PieceColor.WHITE && newRow == 0) {
                    spawnPiece(Queen(PieceColor.WHITE), newRow, newColumn)
                }
            }
        }

    }



    override fun clear() {
        super.clear()
        images.forEach { list -> list.forEach{ it.image = null } }
    }

}