package com.wooftown.gui

import com.wooftown.core.pieces.PieceColor
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

/**
 * Dialog which says who is win
 * @param color - winner's color
 * @see dialog when you can see who is win and exit from it
 */
class WinnerDialog(color: PieceColor) : Dialog<ButtonType>() {
    init {
        title = "Congratulation!!!"
        with(dialogPane) {
            headerText = "$color is win"
            buttonTypes.add(ButtonType("You can restart game in menu", ButtonBar.ButtonData.OK_DONE))
        }
    }
}
