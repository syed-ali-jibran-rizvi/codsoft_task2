package com.example.aitictactoe;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private char[][] board = {{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};
    private boolean playerTurn = true; // true if player's turn, false if computer's turn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set click listeners for each ImageView (representing cells)
        findViewById(R.id.cell1).setOnClickListener(v -> onCellClick(0, 0));
        findViewById(R.id.cell2).setOnClickListener(v -> onCellClick(0, 1));
        findViewById(R.id.cell3).setOnClickListener(v -> onCellClick(0, 2));
        findViewById(R.id.cell4).setOnClickListener(v -> onCellClick(1, 0));
        findViewById(R.id.cell5).setOnClickListener(v -> onCellClick(1, 1));
        findViewById(R.id.cell6).setOnClickListener(v -> onCellClick(1, 2));
        findViewById(R.id.cell7).setOnClickListener(v -> onCellClick(2, 0));
        findViewById(R.id.cell8).setOnClickListener(v -> onCellClick(2, 1));
        findViewById(R.id.cell9).setOnClickListener(v -> onCellClick(2, 2));
    }

    private void onCellClick(int row, int col) {
        if (board[row][col] == ' ' && playerTurn) {
            // Player's move
            board[row][col] = 'X';
            updateUI();
            if (checkWin('X')) {
                endGame("You win!");
            } else if (isBoardFull()) {
                endGame("It's a draw!");
            } else {
                playerTurn = false;
                // Computer's move (call minimax function here)
                computerMove();
            }
        }
    }

    private void computerMove() {
        new Handler().postDelayed(() -> {
            int[] bestMove = minimax(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board[bestMove[0]][bestMove[1]] = 'O';
            updateUI();
            if (checkWin('O')) {
                endGame("Computer wins!");
            } else if (isBoardFull()) {
                endGame("It's a draw!");
            } else {
                playerTurn = true;
            }
        }, 500);

    }

    private int[] minimax(char[][] currentBoard, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int[] bestMove = {-1, -1};
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentBoard[i][j] == ' ') {
                    currentBoard[i][j] = maximizingPlayer ? 'O' : 'X';

                    int score = minimaxScore(currentBoard, depth + 1, alpha, beta, !maximizingPlayer);

                    currentBoard[i][j] = ' ';  // Undo the move

                    if ((maximizingPlayer && score > bestScore) || (!maximizingPlayer && score < bestScore)) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }

                    if (maximizingPlayer) {
                        alpha = Math.max(alpha, bestScore);
                    } else {
                        beta = Math.min(beta, bestScore);
                    }

                    if (beta <= alpha) {
                        break;  // Alpha-beta pruning
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimaxScore(char[][] currentBoard, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (checkWin('O')) {
            return 1;  // Computer wins
        } else if (checkWin('X')) {
            return -1;  // Human player wins
        } else if (isBoardFull()) {
            return 0;  // It's a draw
        }

        if (depth >= 8) {
            return 0;  // Adjust depth for complexity
        }

        int score = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentBoard[i][j] == ' ') {
                    currentBoard[i][j] = maximizingPlayer ? 'O' : 'X';
                    int currentScore = minimaxScore(currentBoard, depth + 1, alpha, beta, !maximizingPlayer);
                    currentBoard[i][j] = ' ';  // Undo the move

                    if (maximizingPlayer) {
                        // Prioritize center and corners
                        int positionScore = getPositionScore(i, j);
                        currentScore += positionScore;

                        score = Math.max(score, currentScore);
                        alpha = Math.max(alpha, score);
                    } else {
                        score = Math.min(score, currentScore);
                        beta = Math.min(beta, score);
                    }

                    if (beta <= alpha) {
                        break;  // Alpha-beta pruning
                    }
                }
            }
        }

        return score;
    }

    // Helper method to assign scores based on the position on the board
    private int getPositionScore(int row, int col) {
        if ((row == 0 || row == 2) && (col == 0 || col == 2)) {
            return 3;  // Corners have higher score
        } else if (row == 1 && col == 1) {
            return 5;  // Center has the highest score
        } else {
            return 1;  // Other positions have a lower score
        }
    }





    private boolean checkWin(char player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false; // There is an empty cell, so the board is not full
                }
            }
        }
        return true; // No empty cell, the board is full
    }

    private void endGame(String message) {
        // Display the end game message (win, draw, or lose)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Reset the game state if needed
        resetGame();
    }

    private void resetGame() {
        // Clear the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        // Reset playerTurn to true for the next game
        playerTurn = true;
        // Update the UI to reflect the cleared board
        updateUI();
    }

    private void updateUI() {
        // Loop through the 'board' array to update ImageView sources
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int cellId = getResources().getIdentifier("cell" + (i * 3 + j + 1), "id", getPackageName());
                ImageView cellImageView = findViewById(cellId);

                if (board[i][j] == 'X') {
                    cellImageView.setImageResource(R.drawable.xiconnobg); // Set X image
                } else if (board[i][j] == 'O') {
                    cellImageView.setImageResource(R.drawable.onobg); // Set O image
                } else {
                    cellImageView.setImageResource(R.drawable.nobg); // Set empty cell image
                }
            }
        }
    }
}
