package com.wooftown.controll

import com.wooftown.core.pieces.Piece
import com.wooftown.core.PieceColor
import com.wooftown.gui.DeskGUI

class DragController() {

    private lateinit var desk : DeskGUI

    fun setDeskPointer(deskGUI: DeskGUI) {
        desk = deskGUI
    }

    private var lastChosenPieceMovies = mutableListOf<Pair<Int, Int>>()

    private var turnNow = PieceColor.WHITE

    fun getTurn() = turnNow

    private lateinit var start : Pair<Int,Int>

    private lateinit var end : Pair<Int,Int>

    fun setStart(row : Int,column:Int){
        start = row to column
    }

    fun setEnd(row: Int,column: Int){
        end = row to column
    }

    fun handle() {
        if (desk[start.first, start.second] is Piece && desk[start.first, start.second]!!.color == turnNow) {
            lastChosenPieceMovies = desk.getPossibleMovies(start.first, start.second).toMutableList()
            if ( end.first to end.second in lastChosenPieceMovies.drop(1)) {
                val oldRow = lastChosenPieceMovies.first().first
                val oldColumn = lastChosenPieceMovies.first().second
                lastChosenPieceMovies.removeAt(0)
                desk.movePiece(oldRow, oldColumn, end.first, end.second)
                turnNow = turnNow.opposite()
                lastChosenPieceMovies.clear()
                return
            }
        }

    }


    fun clear() {
        desk.clear()
        turnNow = PieceColor.WHITE
    }

}