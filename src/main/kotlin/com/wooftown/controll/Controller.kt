package com.wooftown.controll

import com.wooftown.core.pieces.Piece
import com.wooftown.core.pieces.PieceColor
import com.wooftown.gui.DeskGUI
import javafx.scene.paint.Color

class Controller() {

    private lateinit var desk : DeskGUI

    fun setDeskPointer(deskGUI: DeskGUI) {
        desk = deskGUI
    }

    private var lastChosenPieceMovies = mutableListOf<Pair<Int, Int>>()

    private var lastChosenPiece = false

    private var turnNow = PieceColor.WHITE

    fun getTurn() = turnNow

    fun handle(row: Int, column: Int) {
        if (lastChosenPiece && row to column in lastChosenPieceMovies.drop(1)) {
            val oldRow = lastChosenPieceMovies.first().first
            val oldColumn = lastChosenPieceMovies.first().second
            lastChosenPieceMovies.removeAt(0)
            desk.movePiece(oldRow, oldColumn, row, column)
            turnNow = turnNow.opposite()
            disableHint()
            lastChosenPieceMovies.clear()
            lastChosenPiece = false
            return
        }
        if (lastChosenPiece) {
            disableHint()
            lastChosenPieceMovies.clear()
            lastChosenPiece = false
            return
        }
        if (!lastChosenPiece && desk[row, column] is Piece && desk[row, column]!!.color == turnNow) {
            lastChosenPiece = true
            lastChosenPieceMovies = desk.getPossibleMovies(row, column).toMutableList()
            enableHint()
            return
        }
    }

    private fun enableHint() {
        for ((x, y) in lastChosenPieceMovies.drop(1)) {
            if ((x + y) % 2 == 0) {
                desk.setCellColor(x, y, Color.rgb(175, 237, 173))
            } else {
                desk.setCellColor(x, y, Color.rgb(109, 181, 99))

            }
        }
    }

    private fun disableHint() {
        for ((x, y) in lastChosenPieceMovies) {
            if ((x + y) % 2 == 0) {
                desk.setCellColor(x, y, Color.rgb(240, 217, 181))
            } else {
                desk.setCellColor(x, y, Color.rgb(181, 136, 99))
            }

        }
    }

    fun clear() {
        desk.clear()
        disableHint()
        turnNow = PieceColor.WHITE
    }

}