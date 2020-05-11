package com.wooftown.controll

import com.wooftown.core.PieceColor
import com.wooftown.core.pieces.Piece
import com.wooftown.gui.DeskGUI

class DragController {
    private lateinit var desk: DeskGUI

    fun setDeskPointer(deskGUI: DeskGUI) {
        desk = deskGUI
    }

    private var turnNow = PieceColor.WHITE

    fun getTurn() = turnNow

    fun handle(fromRow: Int, fromColumn: Int, toRow: Int?, toColumn: Int?) {
        if (toRow == null || toColumn == null) {
            return
        }
        if (desk[fromRow, fromColumn] is Piece && desk[fromRow, fromColumn]!!.color == turnNow) {
            if (toRow to toColumn in desk.getPossibleMoves(fromRow, fromColumn)) {
                desk.movePiece(fromRow, fromColumn, toRow, toColumn)
                turnNow = turnNow.opposite()
            }
        }
    }

    fun clear() {
        desk.clear()
        turnNow = PieceColor.WHITE
    }

    override fun hashCode(): Int = desk.hashCode() * 31 + turnNow.hashCode()

    override fun equals(other: Any?): Boolean = other is DragController
            && desk == other.desk
            && turnNow == other.turnNow
}