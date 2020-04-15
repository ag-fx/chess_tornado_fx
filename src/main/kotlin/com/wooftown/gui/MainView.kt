package com.wooftown.gui

import com.wooftown.core.ChessBoard
import com.wooftown.core.pieces.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*
import kotlin.math.abs

/**
 * Main view
 */
class MainView : View("ChesterChess") {
    /**
     * root of stage
     */
    override val root = BorderPane()

    /**
     * Game desk
     */
    private val desk = ChessBoard()

    /**
     * List for saving and chaning rectangles and images on it
     */
    private val nodesPairs = MutableList(8) { MutableList(8) { Rectangle() to ImageView() } }

    /**
     * Movies of last chosen piece
     */
    private var lastChosenPieceMovies = mutableListOf<Pair<Int, Int>>()

    /**
     * True piece if chosen
     */
    private var lastChosenPiece = false

    /**
     * Who's turn now
     */
    private var turnNow = PieceColor.WHITE

    /**
     *  Show Which player turn
     */
    private var statusText = text("")

    /**
     * Make desk and start game
     */
    init {
        currentStage!!.icons.add(Image("file:src\\main\\resources\\icon.png"))

        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Restart").action {
                                restartGame()
                            }
                            separator()
                            item("Exit").action {
                                this@MainView.close()
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
                    for (row in 0..7) {
                        row {
                            for (column in 0..7) {
                                stackpane {
                                    val rectangle = rectangle {
                                        fill = if ((row + column) % 2 == 0) {
                                            Color.rgb(240, 217, 181)
                                        } else {
                                            Color.rgb(181, 136, 99)
                                        }
                                        width = 100.0
                                        height = 100.0
                                    }
                                    val image = imageview {
                                        image = null
                                    }
                                    nodesPairs[row][column] = rectangle to image
                                    setOnMouseClicked {
                                        handleClick(row, column)
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        spawnAllPieces()

        primaryStage.isResizable = false
    }

    /**
     * Controller for cells
     * @param row - row of gridpane
     * @param column - column of gridpane
     */
    private fun handleClick(row: Int, column: Int) {
        if (lastChosenPiece && row to column in lastChosenPieceMovies.drop(1)) {
            val oldRow = lastChosenPieceMovies.first().first
            val oldColumn = lastChosenPieceMovies.first().second
            lastChosenPieceMovies.removeAt(0)
            movePiece(oldRow, oldColumn, row, column)
            turnNow = turnNow.opposite()
            disableHint()
            lastChosenPieceMovies.clear()
            lastChosenPiece = false
            updateStatus()
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

    /**
     * Change game status after turn and checking for game winner
     * @see statusText changes
     */
    private fun updateStatus() {
        statusText.apply {
            text = if (turnNow == PieceColor.WHITE) {
                "White's turn"
            } else {
                "Black's turn"
            }
        }
        if (desk.checkLooser() != null) {
            statusText.apply {
                text = if (desk.checkLooser()!!.opposite() == PieceColor.WHITE) {
                    "White win"
                } else {
                    "Black win"
                }
            }
            WinnerDialog(desk.checkLooser()!!.opposite()).showAndWait()
        }
    }

    /**
     * Movie piece on desk
     * @param row - x cords of piece to move
     * @param column - y cords of piece to move
     * @param newRow - x cords to move piece
     * @param newColumn - y cords to move piece
     */
    private fun movePiece(row: Int, column: Int, newRow: Int, newColumn: Int) {


        if (desk[row, column] is King) {
            val castleX = if (desk[row, column]!!.color == PieceColor.WHITE) {
                7
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

        val deleted = desk[newRow, newColumn]
        desk[newRow, newColumn] = desk[row, column]
        desk[row, column] = null
        nodesPairs[newRow][newColumn].second.apply {
            image = nodesPairs[row][column].second.image
        }
        nodesPairs[row][column].second.apply {
            image = null
        }

        if (desk[newRow, newColumn] is Pawn) {

            (desk[newRow, newColumn] as Pawn).moveDouble = abs(newRow - row) == 2

            if (abs(column - newColumn) == 1 && deleted == null) {
                if (desk[newRow, newColumn]!!.color == PieceColor.WHITE) {
                    despairPiece(newRow + 1, newColumn)
                } else {
                    despairPiece(newRow - 1, newColumn)
                }
            }
            if (desk[newRow, newColumn]!!.color == PieceColor.BLACK && newRow == 7) {
                spawnPiece(Queen(PieceColor.BLACK), newRow, newColumn)
            } else {
                if (desk[newRow, newColumn]!!.color == PieceColor.WHITE && newRow == 0) {
                    spawnPiece(Queen(PieceColor.WHITE), newRow, newColumn)
                }
            }
        }

    }

    /**
     * Restarting game
     */
    private fun restartGame() {
        desk.clear()
        turnNow = PieceColor.WHITE
        for (i in 0..7) {
            for (j in 0..7) {
                nodesPairs[i][j].second.apply {
                    image = null
                }
            }
        }
        spawnAllPieces()
        disableHint()
    }

    /**
     * Enable movies hint
     * Color of squares are changed
     */
    private fun enableHint() {
        for ((x, y) in lastChosenPieceMovies.drop(1)) {
            nodesPairs[x][y].first.apply {
                fill = if ((x + y) % 2 == 0) {
                    Color.rgb(175, 237, 173)
                } else {
                    Color.rgb(109, 181, 99)
                }
            }
        }
    }

    /**
     * Disable movies hint
     * Color of squares are normal
     */
    private fun disableHint() {
        for ((x, y) in lastChosenPieceMovies) {
            nodesPairs[x][y].first.apply {
                fill = if ((x + y) % 2 == 0) {
                    Color.rgb(240, 217, 181)
                } else {
                    Color.rgb(181, 136, 99)
                }
            }
        }
    }

    /**
     * Spawning all pieces on start position
     */
    private fun spawnAllPieces() {
        for (column in 0..7) {
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

    /**
     * Deleting piece
     * @param x - x cords of piece
     * @param y -y cords of piece
     */
    private fun despairPiece(x: Int, y: Int) {
        desk[x, y] = null
        nodesPairs[x][y].second.apply {
            image = null
        }
    }

    /**
     * @param piece - what piece we will spawn
     * @param x - x cords of piece
     * @param y -y cords of piece
     * Image of piece in our cords
     */
    private fun spawnPiece(piece: Piece, x: Int, y: Int) {
        desk[x, y] = piece
        nodesPairs[x][y].second.apply {
            image = Image("file:src\\main\\resources\\${piece}.png")
        }
    }

}


