package MokuGame.Service;

import MokuGame.Core.GoMokuBoard;
import org.slf4j.Logger;                // instead of writing  System.out.println("Game started"), we use logger;
import org.slf4j.LoggerFactory;

public class GoMoKuGameService {

    private static final Logger logger = LoggerFactory.getLogger(GoMoKuGameService.class);
    private static final int win_count = 5;  //5 in a row to win
    private GoMokuBoard board;  // Called the GoMokuBoard class

    private char currentPlayer;
    private boolean gameOver;
    private char winner;

    /**
     * Creates a new game service with the specified board.
     *
     * @param board the game board to use
     */
    public GoMoKuGameService(GoMokuBoard board) {
        this.board = board;
        this.currentPlayer = GoMokuBoard.Player1;
        this.gameOver = false;
        this.winner = GoMokuBoard.Empty;
        logger.info("New game starting..letsgoooo {}x{}", board.getRows(), board.getColumns());
        // braces inside shows that we have to input two values
    }

    /**
     * Gets the current game board.
     *
     * @return the board
     */
    public GoMokuBoard getBoard() {
        return board;
    }

    /**
     * Gets the current player.
     *
     * @return the current player character
     */
    public char getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the winner of the game.
     *
     * @return returns the winner
     */
    public char getWinner() {
        return winner;
    }

    public boolean isGameOver() {                 // when the game is over
        return gameOver;
    }

    /**
     * To check if a move is legal.
     * the move is valid and the cell is empty.
     *
     * @param row the row index
     * @param column the column index
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int row, int column) {
        if (!board.isValidPosition(row, column)) {
            logger.debug("Invalid: position ({}, {}) is out of bounds", row, column);
            return false;
        }
        if (!board.isEmpty(row, column)) {
            logger.debug("Invalid : position ({}, {}) is already occupied", row, column);
            return false;
        }
        return true;
    }

    /**
     * Makes a move at the specified position for the current player.
     *
     * @param row the row index
     * @param column the column index
     * @return true if the move was successful, false otherwise
     */
    public boolean makeMove(int row, int column) {
        if (gameOver) {
            logger.warn("game is already over");
            return false;
        }

        if (!isValidMove(row, column)) {
            logger.warn("Invalid move attempted by player {} at ({}, {})", currentPlayer, row, column);
            return false;
        }
        board.setCell(row, column, currentPlayer);
        logger.info("Player {} placed at ({}, {})", currentPlayer, row, column);

        if (checkWin(row, column)) {
            gameOver = true;
            winner = currentPlayer;
            logger.info("Game over! Player {} wins!", winner);
            return true;
        }
        if (isBoardFull()) {
            gameOver = true;
            logger.info("Game over! Board is full - it's a draw!");
            return true;
        }

        switchPlayer();
        return true;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == GoMokuBoard.Player1) ? GoMokuBoard.Player2 : GoMokuBoard.Player1;
        logger.debug("Current player switched to {}", currentPlayer);
    }

    /**
     * Checks if the board is completely full.
     *
     * @return true if the board is full, false otherwise
     */
    public boolean isBoardFull() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (board.isEmpty(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks if the last move at the specified position resulted in a win.
     *
     * @param row the row of the last move
     * @param column the column of the last move
     * @return true if the move resulted in a win, else false
     */

    public boolean checkWin(int row, int column) {
        char player = board.getCell(row, column);

        if (countDirection(row, column, 0, 1, player) +
                countDirection(row, column, 0, -1, player) + 1 >= win_count) {
            logger.debug("A win: horizontal line for player {}", player);
            return true;
        }
        if (countDirection(row, column, 1, 0, player) +
                countDirection(row, column, -1, 0, player) + 1 >= win_count) {
            logger.debug("A win: vertical line for player {}", player);
            return true;
        }
        if (countDirection(row, column, 1, 1, player) +
                countDirection(row, column, -1, -1, player) + 1 >= win_count) {
            logger.debug("A win: diagonal line for player {}", player);
            return true;
        }
        if (countDirection(row, column, 1, -1, player) +
                countDirection(row, column, -1, 1, player) + 1 >= win_count) {
            logger.debug("A win: anti-diagonal line for player {}", player);
            return true;
        }
        return false;
    }

    /**
     * Counts how many of the player's stones are lined up in one direction.
     *
     * Imagine you just placed an X. This method looks left, right, up, down,
     * or diagonally — one way at a time — and counts: "1, 2, 3, 4, 5...!"
     * It stops counting when it hits an empty spot or the opponent's stone.
     *
     * We use it to check: "Did this move make 5 in a row? → WIN!"
     *
     * @param row           where we just placed the stone
     * @param column        where we just placed the stone
     * @param deltaRow      which way are we looking? (up/down/straight)
     * @param deltaColumn   which way are we looking? (left/right/straight)
     * @param player        'X' or 'O' — whose stones are we counting?
     * @return              how many stones in a row in that direction
     */

    private int countDirection(int row, int column, int deltaRow, int deltaColumn, char player) {
        int count = 0;
        int r = row + deltaRow;
        int c = column + deltaColumn;

        while (board.isValidPosition(r, c) && board.getCell(r, c) == player) {
            count++;
            r += deltaRow;
            c += deltaColumn;
        }
        return count;
    }


    public void resetGame() {
        board.clear();
        currentPlayer = GoMokuBoard.Player1;
        gameOver = false;
        winner = GoMokuBoard.Empty;
        logger.info("Game reset");
    }

    /**
     * Sets a new board and starts a new game.
     *
     * @param newBoard the new board to use
     */
    public void setBoard(GoMokuBoard newBoard) {
        this.board = newBoard;
        this.currentPlayer = GoMokuBoard.Player1;
        this.gameOver = false;
        this.winner = GoMokuBoard.Empty;
        logger.info("Board changed to size {}x{}", newBoard.getRows(), newBoard.getColumns());
    }
}