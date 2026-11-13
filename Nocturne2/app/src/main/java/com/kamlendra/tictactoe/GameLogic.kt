package com.kamlendra.tictactoe

/**
 * Pure game rules. X is the human / first player, O is the AI / second player.
 * The minimax search is exhaustive for a 3x3 board, so the AI cannot lose.
 */
object GameLogic {
    private val winningLines = listOf(
        intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
    )

    fun winningLine(board: List<String>): List<Int> {
        require(board.size == 9) { "A Tic-Tac-Toe board must contain exactly 9 cells." }
        val line = winningLines.firstOrNull { cells ->
            val mark = board[cells[0]]
            mark.isNotEmpty() && mark == board[cells[1]] && mark == board[cells[2]]
        }
        return line?.toList() ?: emptyList()
    }

    fun winner(board: List<String>): String? {
        val line = winningLine(board)
        return if (line.isEmpty()) null else board[line.first()]
    }

    fun isDraw(board: List<String>): Boolean =
        winner(board) == null && board.none { it.isEmpty() }

    fun bestMove(board: List<String>): Int {
        require(board.size == 9) { "A Tic-Tac-Toe board must contain exactly 9 cells." }
        require(winner(board) == null && board.any { it.isEmpty() }) {
            "No move is available on a finished board."
        }
        var bestScore = Int.MIN_VALUE
        var bestIndex = -1
        for (index in board.indices) {
            if (board[index].isEmpty()) {
                val next = board.toMutableList()
                next[index] = "O"
                val score = minimax(next, maximizing = false, depth = 0)
                if (score > bestScore) {
                    bestScore = score
                    bestIndex = index
                }
            }
        }
        return bestIndex
    }

    private fun minimax(board: MutableList<String>, maximizing: Boolean, depth: Int): Int {
        val winner = winner(board)
        if (winner == "O") return 10 - depth
        if (winner == "X") return depth - 10
        if (board.none { it.isEmpty() }) return 0

        if (maximizing) {
            var best = Int.MIN_VALUE
            for (index in board.indices) {
                if (board[index].isEmpty()) {
                    board[index] = "O"
                    best = maxOf(best, minimax(board, maximizing = false, depth + 1))
                    board[index] = ""
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (index in board.indices) {
                if (board[index].isEmpty()) {
                    board[index] = "X"
                    best = minOf(best, minimax(board, maximizing = true, depth + 1))
                    board[index] = ""
                }
            }
            return best
        }
    }
}
