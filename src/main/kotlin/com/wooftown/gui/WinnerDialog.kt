package com.wooftown.gui

import com.wooftown.core.MyColor
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

/**
 * Dialog which says who is win
 * @param color - winner's color
 * @see dialog when you can see who is win and exit from it
 */
class WinnerDialog(color: MyColor) : Dialog<ButtonType>() {
    init {
        title = "Congratulation!!!"
        with(dialogPane) {
            headerText = "$color is winning"
            buttonTypes.add(ButtonType("Back to desk", ButtonBar.ButtonData.OK_DONE))
        }
    }
}
