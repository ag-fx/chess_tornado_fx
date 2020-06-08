package com.wooftown.gui

import com.wooftown.controll.DragController
import com.wooftown.core.MyColor
import com.wooftown.core.pieces.*
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*


/**
 * Main game view
 */
class MainView : View("TornadoChess") {
    /**
     * Desk Size
     */

    private val deskSize = 8

    /**
     * Core parent
     */
    override val root = BorderPane()

    /**
     * Gaming desk
     */
    private lateinit var desk: DeskGUI

    /**
     * Controller
     */
    private val mouseController = DragController()

    /**
     * Last dropped cell
     */
    private var lastMouseDrop: Pair<Int?, Int?> = null to null

    /**
     * Status of game
     */
    private var statusText = text("")

    /**
     * Status of hints
     */
    private var hintsIsOn = SimpleBooleanProperty(false)

    init {
        primaryStage.icons.add(Image("file:src\\main\\resources\\icon.png"))
        setWindowMinSize(600, 640)

        // stupid code
        primaryStage.widthProperty().addListener { _: ObservableValue<out Number?>?, _: Number?,
                                                   t1: Number ->
            primaryStage.height = t1.toDouble().plus(45)
        }

        primaryStage.heightProperty().addListener { _: ObservableValue<out Number?>?, _: Number?,
                                                    t1: Number ->
            primaryStage.width = t1.toDouble().minus(45)
        }

        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Hint enabled") {
                                checkbox("", hintsIsOn)
                            }
                            separator()
                            item("Restart").action {
                                restartGame()
                            }
                            separator()
                            item("Exit").action {
                                this@MainView.close()
                            }
                        }
                        menu("Theme") {
                            item("Classic").action {
                                desk.setStyle(PieceStyles.CLASSIC)
                            }
                            item("Berlin").action {
                                desk.setStyle(PieceStyles.BERLIN)
                            }
                            item("Metro").action {
                                desk.setStyle(PieceStyles.METRO)
                            }
                            item("Chess24").action {
                                desk.setStyle(PieceStyles.CHESS24)
                            }
                        }
                    }
                    borderpane {
                        center {
                            statusText = text("white's turn") {
                                fill = Color.BLACK
                                font = Font(20.0)
                            }
                        }
                    }
                }
            }
            center {
                gridpane {
                    val setUpCells = List(deskSize) { MutableList(deskSize) { Rectangle() } }
                    val setUpImages = List(deskSize) { MutableList(deskSize) { ImageView() } }
                    for (row in 0 until deskSize) {
                        row {
                            for (column in 0 until deskSize) {
                                stackpane {
                                    setUpCells[row][column] = rectangle {
                                        fill = if ((row + column) % 2 == 0) {
                                            Color.rgb(240, 217, 181)
                                        } else {
                                            Color.rgb(181, 136, 99)
                                        }
                                        widthProperty().bind(root.widthProperty().divide(deskSize))
                                        heightProperty().bind(widthProperty() - (deskSize / 2))
                                    }

                                    setUpImages[row][column] = imageview {
                                        fitHeightProperty().bind(
                                                setUpCells[row][column].heightProperty() / 11 * deskSize)
                                        fitWidthProperty().bind(fitHeightProperty())
                                    }


                                    // EVENT HANDLING!!!!
                                    onDragDetected = EventHandler { event ->
                                        if (hintsIsOn.value && desk[row, column] is Piece &&
                                                desk[row, column]!!.color == mouseController.getTurn()) {
                                            enableHint(row, column)
                                        }
                                        val db: Dragboard = startDragAndDrop(TransferMode.MOVE)
                                        val content = ClipboardContent()
                                        content.putImage(desk.getImage(row, column))
                                        db.setContent(content)
                                        // чтобы фигурка пропадала со своей клетки когда ее берут
                                        desk.setImage(row, column, null)
                                        event.consume()
                                    }

                                    onDragOver = EventHandler { event ->
                                        if (event.gestureSource != this && event.dragboard.hasImage()) {
                                            event.acceptTransferModes(*TransferMode.ANY) // без этого никак, как оказалось
                                        }
                                        event.consume()
                                    }

                                    onDragDropped = EventHandler { event ->
                                        val db = event.dragboard
                                        var success = false
                                        if (db.hasImage()) {
                                            success = true
                                            lastMouseDrop = row to column
                                        }
                                        event.isDropCompleted = success
                                        event.consume()
                                    }

                                    onDragDone = EventHandler { event ->
                                        disableHint(row, column)
                                        val db = event.dragboard
                                        desk.setImage(row, column, db.image)
                                        if (event.transferMode == TransferMode.MOVE) {
                                            mouseController.handle(row, column, lastMouseDrop.first, lastMouseDrop.second)
                                        }
                                        lastMouseDrop = null to null
                                        updateStatus()
                                        event.consume()
                                    }
                                    // EVENT HANDLING
                                }
                            }
                        }
                    }
                    // сделал так, чтобы не идти ещё раз по циклу устанавливая листнеры
                    desk = DeskGUI(setUpCells, setUpImages, deskSize)
                    mouseController.setDeskPointer(desk)
                }
            }
        }
        spawnAllPieces()
    }


    /**
     * updating status after turn
     */
    private fun updateStatus() {
        statusText.apply {
            text = "${mouseController.getTurn()}'s turn"
        }
        if (desk.checkDraw()){
            WinnerDialog(null).showAndWait()
        }

        if (desk.isLooser(mouseController.getTurn())) {
            statusText.apply {
                text = "${mouseController.getTurn().opposite()} win"
            }
            WinnerDialog(mouseController.getTurn().opposite()).showAndWait()
        }
    }

    /**
     * Restarting game
     */
    private fun restartGame() {
        mouseController.clear()
        spawnAllPieces()
        updateStatus()
    }

    /**
     * Spawn all piece on start game
     */
    private fun spawnAllPieces() {
        with(desk) {
            for (column in 0 until deskSize) {
                spawnPiece(Pawn(MyColor.BLACK, this), 1, column)
                spawnPiece(Pawn(MyColor.WHITE, this), 6, column)
            }
            spawnPiece(Rook(MyColor.WHITE, this), 7, 0)
            spawnPiece(Rook(MyColor.WHITE, this), 7, 7)
            spawnPiece(Rook(MyColor.BLACK, this), 0, 0)
            spawnPiece(Rook(MyColor.BLACK, this), 0, 7)
            spawnPiece(Queen(MyColor.WHITE, this), 7, 3)
            spawnPiece(Queen(MyColor.BLACK, this), 0, 3)
            spawnPiece(King(MyColor.BLACK, this), 0, 4)
            spawnPiece(King(MyColor.WHITE, this), 7, 4)
            spawnPiece(Knight(MyColor.WHITE, this), 7, 1)
            spawnPiece(Knight(MyColor.WHITE, this), 7, 6)
            spawnPiece(Knight(MyColor.BLACK, this), 0, 1)
            spawnPiece(Knight(MyColor.BLACK, this), 0, 6)
            spawnPiece(Bishop(MyColor.BLACK, this), 0, 2)
            spawnPiece(Bishop(MyColor.BLACK, this), 0, 5)
            spawnPiece(Bishop(MyColor.WHITE, this), 7, 5)
            spawnPiece(Bishop(MyColor.WHITE, this), 7, 2)
        }
    }

    /**
     * Enable hints which show possible moves for piece
     * Usually work in cycle with cells which need to light
     * @param row - row
     * @param column - column
     */
    private fun enableHint(row: Int, column: Int) {
        for ((x, y) in desk.getPossibleMoves(row, column)) {
            if ((x + y) % 2 == 0) {
                desk.setCellColor(x, y, Color.rgb(175, 237, 173))
            } else {
                desk.setCellColor(x, y, Color.rgb(109, 181, 99))

            }
        }
    }

    /**
     * Disable hints
     */
    private fun disableHint(row: Int, column: Int) {
        for ((x, y) in desk.getPossibleMoves(row, column)) {
            if ((x + y) % 2 == 0) {
                desk.setCellColor(x, y, Color.rgb(240, 217, 181))
            } else {
                desk.setCellColor(x, y, Color.rgb(181, 136, 99))
            }

        }
    }

}
