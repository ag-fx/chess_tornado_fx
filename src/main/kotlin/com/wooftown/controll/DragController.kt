package com.wooftown.controll

import com.wooftown.core.MyColor
import com.wooftown.core.pieces.Piece
import com.wooftown.gui.DeskGUI

/**
 * Class which implements controller.
 * Do turns, clear desk and control whose turn.
 */
class DragController {
    /**
     * Our game desk, we need it for change it
     */
    private lateinit var desk: DeskGUI


    /**
     * Set pointer of desk
     * @param deskGUI - our desk
     */
    fun setDeskPointer(deskGUI: DeskGUI) {
        desk = deskGUI
    }


    /**
     * Whose turn now
     */
    private var turnNow = MyColor.WHITE

    /**
     * Getter of turnNow
     * @return color of player
     */
    fun getTurn() = turnNow

    /**
     * Handle move from start point to end point.
     * If move is impossible do nothing
     * If we do not drag correctly do nothing
     * @param fromRow - start row
     * @param fromColumn - start column
     * @param toRow - final row
     * @param toColumn - final column
     */
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

    /**
     * Clear desk for restarting the game
     */
    fun clear() {
        desk.clear()
        turnNow = MyColor.WHITE
    }

    /**
     * HashCode which depends on desk and whose turn now
     */
    override fun hashCode(): Int = desk.hashCode() * 31 + turnNow.hashCode()

    /**
     * Equals for controllers
     */
    override fun equals(other: Any?): Boolean = other is DragController
            && desk == other.desk
            && turnNow == other.turnNow
}