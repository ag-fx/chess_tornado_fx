package com.wooftown.gui

import com.wooftown.controll.DragController
import com.wooftown.core.BOARD_SIZE
import com.wooftown.core.PieceColor
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
    private val controller = DragController()

    /**
     * Last dropped cell
     */
    private var lastDrop: Pair<Int?, Int?> = null to null

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
        setWindowMinSize(800, 850)

        // stupid code
        primaryStage.widthProperty().addListener {
            _: ObservableValue<out Number?>?, _: Number?,
            t1: Number -> primaryStage.height = t1.toDouble().plus(35) }

        primaryStage.heightProperty().addListener {
            _: ObservableValue<out Number?>?, _: Number?,
            t1: Number -> primaryStage.width = t1.toDouble().minus(35) }

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
                            statusText = text("White's turn") {
                                fill = Color.BLACK
                                font = Font(20.0)
                            }
                        }
                    }
                }
            }
            center {
                gridpane {
                    val setUpCells = List(BOARD_SIZE) { MutableList(BOARD_SIZE) { Rectangle() } }
                    val setUpImages = List(BOARD_SIZE) { MutableList(BOARD_SIZE) { ImageView() } }
                    for (row in 0 until BOARD_SIZE) {
                        row {
                            for (column in 0 until BOARD_SIZE) {
                                stackpane {
                                    setUpCells[row][column] = rectangle {
                                        fill = if ((row + column) % 2 == 0) {
                                            Color.rgb(240, 217, 181)
                                        } else {
                                            Color.rgb(181, 136, 99)
                                        }
                                        widthProperty().bind(root.widthProperty().divide(BOARD_SIZE))
                                        heightProperty().bind(widthProperty() - 5)
                                    }

                                    setUpImages[row][column] = imageview {
                                        fitHeightProperty().bind(
                                                setUpCells[row][column].heightProperty() / 14 * BOARD_SIZE)
                                        fitWidthProperty().bind(fitHeightProperty())
                                    }

                                    // EVENT HANDLING!!!!
                                    onDragDetected = EventHandler { event ->
                                        if (hintsIsOn.value && desk[row, column] is Piece &&
                                                desk[row, column]!!.color == controller.getTurn()) {
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
                                            lastDrop = row to column
                                        }
                                        event.isDropCompleted = success
                                        event.consume()
                                    }

                                    onDragDone = EventHandler { event ->
                                        disableHint(row, column)
                                        val db = event.dragboard
                                        desk.setImage(row, column, db.image)
                                        if (event.transferMode == TransferMode.MOVE) {
                                            controller.handle(row, column, lastDrop.first, lastDrop.second)
                                        }
                                        lastDrop = null to null
                                        updateStatus()
                                        event.consume()
                                    }
                                    // EVENT HANDLING
                                }
                            }
                        }
                    }
                    desk = DeskGUI(setUpCells, setUpImages)
                    controller.setDeskPointer(desk)
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
            text = if (controller.getTurn() == PieceColor.WHITE) {
                "White's turn"
            } else {
                "Black's turn"
            }
        }
        if (desk.isLooser(controller.getTurn())) {
            statusText.apply {
                text = if (controller.getTurn().opposite() == PieceColor.WHITE) {
                    "White win"
                } else {
                    "Black win"
                }
            }
            WinnerDialog(controller.getTurn().opposite()).showAndWait()
        }
    }

    /**
     * Restarting game
     */
    private fun restartGame() {
        controller.clear()
        spawnAllPieces()
        updateStatus()
    }

    /**
     * Spawn all piece on start game
     */
    private fun spawnAllPieces() {
        with(desk) {
            for (column in 0 until BOARD_SIZE) {
                spawnPiece(Pawn(PieceColor.BLACK), 1, column)
                spawnPiece(Pawn(PieceColor.WHITE), 6, column)
            }
            spawnPiece(Rook(PieceColor.WHITE), 7, 0)
            spawnPiece(Rook(PieceColor.WHITE), 7, 7)
            spawnPiece(Rook(PieceColor.BLACK), 0, 0)
            spawnPiece(Rook(PieceColor.BLACK), 0, 7)
            spawnPiece(Queen(PieceColor.WHITE), 7, 3)
            spawnPiece(Queen(PieceColor.BLACK), 0, 3)
            spawnPiece(King(PieceColor.BLACK), 0, 4)
            spawnPiece(King(PieceColor.WHITE), 7, 4)
            spawnPiece(Knight(PieceColor.WHITE), 7, 1)
            spawnPiece(Knight(PieceColor.WHITE), 7, 6)
            spawnPiece(Knight(PieceColor.BLACK), 0, 1)
            spawnPiece(Knight(PieceColor.BLACK), 0, 6)
            spawnPiece(Bishop(PieceColor.BLACK), 0, 2)
            spawnPiece(Bishop(PieceColor.BLACK), 0, 5)
            spawnPiece(Bishop(PieceColor.WHITE), 7, 5)
            spawnPiece(Bishop(PieceColor.WHITE), 7, 2)
        }
    }

    private fun enableHint(row: Int, column: Int) {
        for ((x, y) in desk.getPossibleMovies(row, column)) {
            if ((x + y) % 2 == 0) {
                desk.setCellColor(x, y, Color.rgb(175, 237, 173))
            } else {
                desk.setCellColor(x, y, Color.rgb(109, 181, 99))

            }
        }
    }

    private fun disableHint(row: Int, column: Int) {
        for ((x, y) in desk.getPossibleMovies(row, column)) {
            if ((x + y) % 2 == 0) {
                desk.setCellColor(x, y, Color.rgb(240, 217, 181))
            } else {
                desk.setCellColor(x, y, Color.rgb(181, 136, 99))
            }

        }
    }

}



