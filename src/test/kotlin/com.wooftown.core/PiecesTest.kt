package com.wooftown.core

import com.wooftown.core.pieces.*
import org.junit.Assert.*
import org.junit.Test


val desk = ChessBoard()
fun movies(x: Int, y: Int) = desk[x, y]!!.getPossibleMovies(x, y).toSet()

fun squares(list: List<String>): Set<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    for (notation in list) {
        result.add(notation[0] - 'a' to 8 - notation[1].toString().toInt())
    }
    return result.toSet()
}

class PiecesTest {

    class BishopTest {

        @Test
        fun getMovies() {
            desk.clear()
            desk[0, 0] = Bishop(PieceColor.BLACK)
            assertEquals(squares(listOf("a8", "b7", "c6", "d5", "e4", "f3", "g2", "h1")), movies(0, 0))
            desk[4, 4] = Bishop(PieceColor.WHITE)
            assertEquals(squares(listOf("e4", "f3", "g2", "h1", "d5", "c6", "b7", "a8", "f5", "g6", "h7", "d3", "c2", "b1")), movies(4, 4))
            desk[0, 1] = Bishop(PieceColor.WHITE)
            desk[1, 1] = Bishop(PieceColor.WHITE)
            desk[1, 0] = Bishop(PieceColor.WHITE)
            assertEquals(squares(listOf("a8", "b7")), movies(0, 0))
        }


    }

    class KingTest {
        @Test
        fun getMovies() {
            desk.clear()
            desk[0, 4] = King(PieceColor.BLACK)
            desk[7, 4] = King(PieceColor.WHITE)
            val blackMovies = movies(0, 4)
            assertEquals(squares(listOf("a4", "b3", "b4", "b5", "a5", "a3")), blackMovies)
            desk[1, 4] = King(PieceColor.WHITE)
            desk[0, 3] = King(PieceColor.WHITE)
            desk[0, 5] = King(PieceColor.WHITE)
            desk[1, 5] = King(PieceColor.WHITE)
            desk[1, 3] = King(PieceColor.WHITE)
            assertEquals(blackMovies, movies(0, 4))
            desk[6, 4] = King(PieceColor.WHITE)
            desk[7, 3] = King(PieceColor.WHITE)
            desk[7, 5] = King(PieceColor.WHITE)
            desk[6, 5] = King(PieceColor.WHITE)
            desk[6, 3] = King(PieceColor.WHITE)
            assertEquals(listOf<Pair<Int, Int>>(), desk[7, 4]!!.getPossibleMovies(7, 4).drop(1).toSet())
        }

        @Test
        fun castling() {
            desk.clear()
            desk[0, 4] = King(PieceColor.BLACK)
            desk[0, 0] = Rook(PieceColor.BLACK)
            assertTrue(Pair(0, 2) in movies(0, 4))
            desk[0, 1] = Knight(PieceColor.BLACK)
            assertFalse(Pair(0, 2) in movies(0, 4))
        }
    }

    class KnightTest {

        @Test
        fun getMovies() {
            desk.clear()
            desk[1, 1] = Knight(PieceColor.BLACK)
            assertEquals(squares(listOf("b7", "c5", "a5", "d6", "d8")), movies(1, 1))
            desk[0, 3] = Knight(PieceColor.BLACK)
            assertTrue(0 to 3 !in movies(1, 1))
        }

    }

    class PawnTest {
        @Test
        fun getMovies() {
            desk.clear()
            desk[1, 1] = Pawn(PieceColor.BLACK)
            assertEquals(setOf(1 to 1, 3 to 1, 2 to 1), movies(1, 1))
            desk[4, 3] = Pawn(PieceColor.BLACK)
            desk[4, 2] = Pawn(PieceColor.WHITE)
            (desk[4, 2] as Pawn).moveDouble = true
            assertEquals(setOf(4 to 3, 5 to 3, 5 to 2), movies(4, 3))
        }
    }

    class QueenTest {
        @Test
        fun getMovies() {
            desk.clear()
            desk[3, 3] = Queen(PieceColor.WHITE)
            assertEquals(squares(listOf("d5", "e4", "f3", "g2", "h1", "d4", "d3", "d2", "d1", "c4", "b3", "a2",
                    "c5", "b5", "a5", "c6", "b7", "a8", "d6", "d7", "d8", "e6", "f7", "g8", "e5", "f5", "g5", "h5")), movies(3, 3))
            desk[1, 1] = Queen(PieceColor.WHITE)
            desk[6, 6] = Queen(PieceColor.BLACK)
            desk[4, 2] = Queen(PieceColor.WHITE)
            desk[2, 4] = Queen(PieceColor.WHITE)
            assertEquals(squares(listOf("d5", "e4", "f3", "g2", "d4", "d3", "d2", "d1", "c5", "b5", "a5", "c6", "d6"
                    , "d7", "d8", "e5", "f5", "g5", "h5")), movies(3, 3))
        }

    }

    class RookTest {
        @Test
        fun getMovies() {
            desk.clear()
            desk[0, 0] = Rook(PieceColor.BLACK)
            assertEquals(squares(listOf("a8", "a7", "a6", "a5", "a4", "a3", "a2", "a1", "b8", "c8", "d8", "e8", "f8", "g8", "h8")),
                    movies(0, 0))
            desk[5, 0] = Rook(PieceColor.BLACK)
            desk[0, 3] = Rook(PieceColor.WHITE)
            assertEquals(squares(listOf("a8", "a7", "a6", "a5", "b8", "c8", "d8", "e8")), movies(0, 0))

        }
    }
}