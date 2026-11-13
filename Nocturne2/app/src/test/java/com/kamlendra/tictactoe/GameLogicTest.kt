package com.kamlendra.tictactoe

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {
    @Test fun detectsHorizontalWin() {
        val board = listOf("X", "X", "X", "O", "", "O", "", "", "")
        assertEquals("X", GameLogic.winner(board))
        assertEquals(listOf(0, 1, 2), GameLogic.winningLine(board))
    }

    @Test fun detectsDraw() {
        val board = listOf("X", "O", "X", "X", "O", "O", "O", "X", "X")
        assertTrue(GameLogic.isDraw(board))
    }

    @Test fun aiTakesImmediateWin() {
        val board = listOf("O", "O", "", "X", "X", "", "", "", "")
        assertEquals(2, GameLogic.bestMove(board))
    }

    @Test fun aiBlocksImmediateHumanWin() {
        val board = listOf("X", "X", "", "O", "", "", "", "O", "")
        assertEquals(2, GameLogic.bestMove(board))
    }
}
